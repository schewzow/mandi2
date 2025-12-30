import {InputAdornment, TextField, type TextFieldProps} from "@mui/material";
import {useEntityField} from "../../../hooks/useEntityField.ts";
import type {BaseEntity} from "../../../types/base-entity.ts";
import type {StringKeys, StringValue} from "../field-utils";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import {useSaveAcknowledgment} from "../../../hooks/useSaveAcknowledgment.ts";
import {FormattedMessage} from "react-intl";
import type {EntityFieldProps} from "../EntityFieldProps.ts";

export interface EntityTextFieldProps<
    T extends BaseEntity,
    K extends StringKeys<T>
> extends EntityFieldProps<T, K> {
    /** Optional normalization for display/patch (e.g., trim) */
    normalize?: (value: StringValue<T, K>) => StringValue<T, K>;
    /** Optional comparator used to avoid no-op blur patches */
    compare?: (a: (T[K] & string | number | boolean) | undefined, b: T[K] & string | number | boolean) => boolean;
    /** MUI props passthrough */
    fieldProps?: Omit<TextFieldProps, "value" | "onChange" | "onBlur" | "onEnter" | "error" | "helperText">;
};

export function EntityTextField<T extends BaseEntity, K extends StringKeys<T>>(props: EntityTextFieldProps<T, K>) {
    const {
        data,
        errors,
        states,
        setField,
        field,
        label,
        patchOnBlur,
        loading,
        normalize,
        compare,
        fieldProps,
        showAcknowledge = true,
        i18nLabel,
    } = props;

    const {value, error, helperText, disabled, onChange, onBlur, onEnter, state} = useEntityField<T, K>({
        data,
        errors,
        states,
        setField,
        field,
        patchOnBlur,
        loading,
        normalize,
        compare,
    });

    const showAck = useSaveAcknowledgment(state, showAcknowledge);

    function handleOnEnter() {
        onEnter(value as StringValue<T, K>);
    }

    return (
        <TextField
            {...fieldProps}
            fullWidth
            //label={label ?? String(field)}
            label={i18nLabel ? <FormattedMessage id={i18nLabel}/> : label}
            value={value ?? null}
            error={Boolean(error)}
            helperText={helperText}
            onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
                onChange(e.target.value as StringValue<T, K>)
            }
            onBlur={(e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement>) =>
                onBlur(e.target.value as StringValue<T, K>)
            }
            onKeyUp={(e) => {
                if (e.key === 'Enter') {
                    //onEnter(value as StringValue<T, K>);
                    handleOnEnter();
                }
            }}
            disabled={disabled}
            slotProps={{
                input: {
                    endAdornment: (

                        <InputAdornment position="end">
                            {showAck ? (
                                state === "SUCCESS" ? (
                                    <CheckCircleIcon color="success" fontSize="small"/>
                                ) : (
                                    <WarningAmberIcon color="error" fontSize="small"/>
                                )
                            ) : null}
                        </InputAdornment>

                    ),
                },
            }}
        />
    );
}