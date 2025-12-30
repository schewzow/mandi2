import type {BaseEntity} from "../../types/base-entity.ts";
import type {StringKeys} from "./field-utils.ts";
import type {StateType} from "../../hooks/useEntityForm.ts";

type Mode = "onChange" | "onBlur" | "onEnter";

export interface EntityFieldProps<
    T extends BaseEntity,
    K extends StringKeys<T>
>  {
    data: T | null;
    errors: Partial<Record<keyof T, string | undefined>>;
    states: Partial<Record<keyof T, StateType | undefined>>;
    field: K;
    setField: (key: K, value: T[K] & string | number | boolean, mode?: Mode) => unknown;
    showAcknowledge?: boolean;
    label?: string;
    i18nLabel?: string;
    /** If true: patch only on blur; if false: patch on change (debounced by hook) */
    patchOnBlur?: boolean;
    loading?: boolean;
};
