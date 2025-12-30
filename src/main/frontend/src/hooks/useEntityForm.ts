// src/hooks/useEntityForm.ts
import {useCallback, useEffect, useRef, useState} from "react";
import type {BaseEntity} from "../types/base-entity.ts";
import type {EntityAPIType} from "../api/entity-api.ts";
import type {RequestResponse} from "../api/response.ts";
import type {ErrorType, RequestError} from "../api/api-error.ts";
import {useNavigate} from "react-router";

export const StateTypes = ["SUCCESS", "ERROR", "UNDEFINED"] as const;
export type StateType = typeof StateTypes[number];

type UseEntityFormOptions<T extends BaseEntity> = {
    uuid: string;                          // stable identifier from URL/router
    entityApi: EntityAPIType<T>;         // make this generic to the actual entity
    entityPath: string;
    debounceMs?: number;
    /** Extract RequestError from raw patch response (if not provided, default extractor is used). */
    getRequestError?: (response: unknown) => RequestError | null;
    /** Map backend field names to your TS keys (if different). */
    mapFieldKey?: (backendKey: string) => keyof T | null;
    /** Convert ErrorType to display message. */
    toErrorMessage?: (e: ErrorType) => string;
    defaultValues?: Partial<T>;
};

export function useEntityForm<T extends BaseEntity>({
                                                        uuid,
                                                        entityApi,
                                                        entityPath,
                                                        debounceMs = 400,
                                                        getRequestError,
                                                        mapFieldKey,
                                                        toErrorMessage,
                                                        defaultValues = {},
                                                    }: UseEntityFormOptions<T>) {
    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isSaving, setIsSaving] = useState(false);
    const navigate = useNavigate();

// Field errors to bind to TextFields
    const [errors, setErrors] = useState<Partial<Record<keyof T, string | undefined>>>({});
    const [states, setStates] = useState<Partial<Record<keyof T, StateType | undefined>>>({});

    // Optional: expose global form-level backend errors (e.g., show in an <Alert/>)
    const [globalErrors, setGlobalErrors] = useState<string[]>([]);


    // Keep latest values in refs to avoid stale closures in debounced callbacks.
    const dataRef = useRef<T | null>(null);
    const latestIdRef = useRef(uuid);
    const isMountedRef = useRef(true);
    const errorsRef = useRef<Partial<Record<keyof T, string | undefined>>>({});
    //const statesRef = useRef<Partial<Record<keyof T, StateType | undefined>>>({});

    useEffect(() => {
        latestIdRef.current = uuid;
    }, [uuid]);
    useEffect(() => {
        dataRef.current = data;
    }, [data]);
    useEffect(() => {
        errorsRef.current = errors;
    }, [errors]);
    //useEffect(() => { statesRef.current = states; }, [states]);
    useEffect(() => () => {
        isMountedRef.current = false;
    }, []);

    const pendingPatchRef = useRef<Partial<T>>({});
    const patchTimerRef = useRef<number | null>(null);

    // Load entity
    useEffect(() => {
        let cancelled = false;
        setLoading(true);
        setError(null);

        void (async () => {
            try {
                if (uuid === "create") {
                    setData({
                        ...defaultValues
                    } as T);
                } else {
                    const response = (await entityApi.fetch({uuid: uuid})) as RequestResponse<T>;
                    if (!cancelled && response.status === "success" && response.data) {
                        setData(response.data);
                    }
                }
            } catch (e) {
                const err = e as Error & { name?: string };
                if (!cancelled && err.name !== "AbortError") setError(err.message ?? "Unknown error");
            } finally {
                if (!cancelled) setLoading(false);
            }
        })();

        return () => {
            cancelled = true;
            if (patchTimerRef.current) window.clearTimeout(patchTimerRef.current);
        };
    }, [uuid, entityApi]);

// helper
    function oneKeyPatch<T, K extends keyof T>(key: K, value: T[K]): Partial<Pick<T, K>> {
        const obj = {} as Partial<Pick<T, K>>;
        obj[key] = value;
        return obj;
    }

// Drop unchanged keys to avoid no-op PATCHes
    function removeUnchanged(partial: Partial<T>): Partial<T> {
        const current = dataRef.current;
        if (!current) return partial;
        const result: Partial<T> = {};
        (Object.keys(partial) as Array<keyof T>).forEach((key) => {
            const newVal = partial[key];
            const oldVal = current[key];
            const same =
                newVal === oldVal ||
                (typeof newVal === "string" &&
                    typeof oldVal === "string" &&
                    newVal.trim() === oldVal.trim());
            if (!same) result[key] = newVal!;
        });
        return result;
    }

    // Pick current values for given keys from dataRef
    function pickKeysFromData(keys: Array<keyof T>): Partial<T> {
        const current = dataRef.current;
        const out: Partial<T> = {};
        if (!current) return out;
        for (const k of keys) {
            out[k] = current[k];
        }
        return out;
    }

    // Default extractors/mappers if you don't supply custom versions
    const defaultToErrorMessage = (e: ErrorType): string =>
        typeof e === "string" ? e : e?.message ?? (e?.key ? String(e.key) : "Ungültiger Wert");

    const defaultGetRequestError = (resp: RequestResponse<T>): RequestError | null => {
        // Expecting shape like: { status: 'error', error: RequestError }
        //const r = resp as any;
        if (resp.status === "error" && resp.error && resp.error.fields) {
            return resp.error;
        }
        return null;
    };

    const toMsg = toErrorMessage ?? defaultToErrorMessage;
    const getErr = getRequestError ?? defaultGetRequestError;
    const mapKey = (backendKey: string): keyof T | null =>
        mapFieldKey ? mapFieldKey(backendKey) : (backendKey as keyof T);

    function applyBackendErrors(reqErr: RequestError, sentKeys: Array<keyof T>) {

        let fieldErrors: boolean = false;

        // Field-level
        const next: Partial<Record<keyof T, string | undefined>> = {};
        for (const [bk, arr] of Object.entries(reqErr.fields ?? {})) {
            const k = mapKey(bk);
            if (!k) continue;
            const msg = (arr ?? []).map(toMsg).join("\n");
            next[k] = msg || "Ungültiger Wert";
            fieldErrors = true;
        }
        // If a key was sent and the server did NOT complain about it, clear its error
        for (const k of sentKeys) {
            if (fieldErrors) {
                if (!(k in next)) next[k] = "Another field has an error.";
            } else {
                if (!(k in next)) next[k] = undefined;
            }
        }
        setErrors((prev) => ({...prev, ...next}));

        // Global-level
        const globals = (reqErr.global ?? []).map(toMsg).filter(Boolean);
        setGlobalErrors(globals);
    }

    function clearErrorsForKeys(keys: Array<keyof T>) {
        if (!keys.length) return;
        setErrors((prev) => {
            const next = {...prev};
            for (const k of keys) next[k] = undefined;
            return next;
        });
    }

    function setStateForKeys(keys: Array<keyof T>, state: StateType) {
        if (!keys.length) return;
        setStates((prev) => {
            const next = {...prev};
            for (const k of keys) next[k] = state;
            return next;
        });
    }

    // --- Debounced PATCH that also includes error fields ---
    const schedulePatch = useCallback((partial: Partial<T>) => {
        // First drop unchanged user-edited keys
        const filtered = removeUnchanged(partial);

        // Force-include all fields that currently have an error
        const erroredKeys = Object.entries(errorsRef.current)
            .filter(([, msg]) => typeof msg === "string" && msg)
            .map(([k]) => k as keyof T);

        const forcedErrorFields = pickKeysFromData(erroredKeys);

        // If nothing changed and no error fields, skip
        if (Object.keys(filtered).length === 0 && Object.keys(forcedErrorFields).length === 0) {
            return;
        }

        // Queue both the filtered changes and the forced error fields
        pendingPatchRef.current = {...forcedErrorFields, ...pendingPatchRef.current, ...filtered};

        if (patchTimerRef.current) window.clearTimeout(patchTimerRef.current);
        patchTimerRef.current = window.setTimeout(() => {

            const toSend = {...pendingPatchRef.current};

            pendingPatchRef.current = {};
            setIsSaving(true);

            void (async () => {
                try {
                    const targetId = latestIdRef.current;
                    const response: RequestResponse<T> = targetId === "create" ?
                        await entityApi.create({data: dataRef.current as Partial<T>}) :
                        await entityApi.patch({uuid: targetId, data: toSend});

                    // Handle backend validation errors
                    const reqErr = getErr(response);
                    if (reqErr) {
                        if (isMountedRef.current) {
                            const sentKeys = Object.keys(toSend) as Array<keyof T>;
                            applyBackendErrors(reqErr, sentKeys);
                            setStateForKeys(sentKeys, "ERROR");
                        }
                        return; // do not merge data on validation error
                    }

                    if (targetId === "create") {
                        console.log("create response: ", response);
                        //latestIdRef.current = response.data?.uuid ?? "create";
                        if (response.data?.uuid) {
                            navigate(`/${entityPath}/${response.data.uuid}`, {replace: true});
                        }
                    }

                    // Success: merge and clear errors for sent keys
                    const rr = response as RequestResponse<Partial<T> | T | null | undefined>;
                    if (rr?.status === "success" && rr.data && typeof rr.data === "object") {
                        if (isMountedRef.current) {
                            setData((prev) => (prev ? {...prev, ...(rr.data as Partial<T>)} : prev));
                            const sentKeys = Object.keys(toSend) as Array<keyof T>;
                            clearErrorsForKeys(sentKeys);
                            setStateForKeys(sentKeys, "SUCCESS");
                            setGlobalErrors([]); // clear global errors on success
                        }
                    }
                } catch (e) {
                    if (isMountedRef.current) {
                        setError((e as Error).message ?? "Patch error");
                    }
                } finally {
                    if (isMountedRef.current) setIsSaving(false);
                }
            })();
        }, debounceMs);
    }, [debounceMs, entityApi, getErr, mapFieldKey, toMsg]);

    // Optimistic update; only schedule patch when entity is loaded
    const setField = useCallback(
        <K extends keyof T>(key: K, value: T[K] & string | number | boolean) => {
            //<K extends keyof T>(key: K, value: T[K]) => {

            // Clear error for the field as user edits
            setErrors((prev) => ({...prev, [key]: undefined}));
            // Clear state for the field as user edits
            setStates((prev) => ({...prev, [key]: "UNDEFINED"}));

            setData((prev) => (prev === null ? prev : {...prev, [key]: value}));
            schedulePatch(oneKeyPatch<T, K>(key, value as T[K]) as Partial<T>); // OK
        }, [schedulePatch]);

    return {data, loading, error, setField, isSaving, errors, globalErrors, states};
}
