
// src/hooks/useEntityField.ts
import { useMemo, useState, useCallback } from "react";
import type { BaseEntity } from "../types/base-entity";
import type {StringKeys, StringValue} from "../components/Form/field-utils.ts";
import type {StateType} from "./useEntityForm.ts";

type Mode = "onChange" | "onBlur" | "onEnter";

export type UseEntityFieldOptions<
    T extends BaseEntity,
    K extends StringKeys<T>
> = {
    data: T | null;
    errors: Partial<Record<keyof T, string | undefined>>;
    states: Partial<Record<keyof T, StateType | undefined>>;
    // K is already bound at the options level:
    setField: (key: K, value: StringValue<T, K>, mode?: Mode) => unknown;
    field: K;
    patchOnBlur?: boolean;
    loading?: boolean;
    normalize?: (value: StringValue<T, K>) => StringValue<T, K>;
    compare?: (a: StringValue<T, K> | undefined, b: StringValue<T, K>) => boolean;
};

export function useEntityField<
    T extends BaseEntity,
    K extends StringKeys<T>
>({
      data,
      errors,
      states,
      setField,
      field,
      patchOnBlur = false,
      loading = false,
      normalize,
      compare,
  }: UseEntityFieldOptions<T, K>) {

    const valueFromData = (data?.[field] ?? "") as StringValue<T, K>;
    const [local, setLocal] = useState<StringValue<T, K>>(valueFromData);
    //const [validationError, setValidationError] = useState<string>("");

    // Default normalizer & comparator for strings
    const norm = useMemo(() => normalize ?? ((v: StringValue<T, K>) => v), [normalize]);

    const eq = useMemo(
        () =>
            compare ??
            ((a: StringValue<T, K> | undefined, b: StringValue<T, K>) => {
                if(typeof a === "number") {
                    const naa = a.toString();
                    const bb = (b as unknown as string | undefined)?.trim() ?? "";
                    return naa === bb;
                }
                const aa = (a as unknown as string | undefined)?.trim() ?? "";
                const bb = (b as unknown as string | undefined)?.trim() ?? "";
                return aa === bb;
            }),
        [compare]
    );

// Keep local in sync when patch-on-blur is used
    useCallback(() => {
        if (patchOnBlur) setLocal(valueFromData);
    }, [patchOnBlur, valueFromData]);

    const error = errors[field];
    const helperText = error ?? " ";
    const state = states[field];

    const onChange = useCallback(
        (next: StringValue<T, K>) => {
            const normalized = norm(next);
            if (patchOnBlur) {
                setLocal(normalized);
            } else {
                setField(field, normalized, "onChange");
            }
        },
        [patchOnBlur, field, norm, setField]
    );

    const onBlur = useCallback(
        (currentInput: StringValue<T, K>) => {
            if (!patchOnBlur) return;
            const normalized = norm(currentInput);
            if (eq(valueFromData, normalized)) return; // skip unchanged
            setField(field, normalized, "onBlur");
        },
        [patchOnBlur, field, norm, valueFromData, eq, setField]
    );

    const onEnter = useCallback(
        (currentInput: StringValue<T, K>) => {
            const normalized = norm(currentInput);
            if (eq(valueFromData, normalized)) return; // skip unchanged
            setField(field, normalized, "onEnter");
        },
        [patchOnBlur, field, norm, valueFromData, eq, setField]
    );

    return {

        value: (patchOnBlur ? local : valueFromData) as string, // MUI TextField expects string
        error,
        helperText,
        disabled: loading,
        setLocal: (v: string) => setLocal(v as StringValue<T, K>),
        onChange,
        onBlur,
        onEnter,
        state,
    };
}