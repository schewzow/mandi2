import {useEntityField} from "../../../hooks/useEntityField.ts";
import type {BaseEntity} from "../../../types/base-entity.ts";
import type {StringKeys, StringValue} from "../field-utils";
import {DatePicker, type DatePickerFieldProps} from "@mui/x-date-pickers";
import dayjs, {type Dayjs} from "dayjs";
import {toDbDateString} from "../../../utils/date.ts";
import {Box} from "@mui/material";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import {useSaveAcknowledgment} from "../../../hooks/useSaveAcknowledgment.ts";
import {FormattedMessage} from "react-intl";
import type {EntityFieldProps} from "../EntityFieldProps.ts";

export interface EntityDateFieldProps<
    T extends BaseEntity,
    K extends StringKeys<T>
> extends EntityFieldProps<T, K> {
    /** Optional normalization for display/patch (e.g., trim) */
    normalize?: (value: StringValue<T, K>) => StringValue<T, K>;
    /** Optional comparator used to avoid no-op blur patches */
    compare?: (a: (T[K] & string | number | boolean) | undefined, b: T[K] & string | number | boolean) => boolean;
    /** MUI props passthrough */
    fieldProps?: Omit<DatePickerFieldProps, "value" | "onChange" | "onBlur" | "onEnter" | "error" | "helperText">;
};

export function EntityDateField<T extends BaseEntity, K extends StringKeys<T>>(props: EntityDateFieldProps<T, K>) {
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

    const {value, error, helperText, disabled, onChange, state} = useEntityField<T, K>({
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

    function handleOnChange(value: Dayjs | null) {
        if (value != null) {
            onChange(toDbDateString(value.format('YYYY-MM-DD')) as StringValue<T, K>);
            //onChange((value.format('YYYY-MM-DD')) as StringValue<T, K>);
        }
    }

    return (
        <Box position="relative" display="inline-flex">
            <DatePicker
                {...fieldProps}
                label={i18nLabel ? <FormattedMessage id={i18nLabel}/> : label}
                value={value ? dayjs(value) : null}
                onChange={(e) =>
                    handleOnChange(e)
                }
                disabled={disabled}
                slotProps={{
                    textField: {
                        //fullWidth: fullWidth,
                        variant: 'outlined',
                        error: Boolean(error),
                        helperText: helperText,
                        size: "small",
                    },
                }}
            />
            {showAck && (
                <Box
                    position="absolute"
                    right={40}      // leave space for the calendar button
                    top="30%"
                    sx={{transform: "translateY(-30%)", pointerEvents: "none"}}
                >
                    {state === "SUCCESS" ? (
                        <CheckCircleIcon color="success" fontSize="small"/>
                    ) : (
                        <WarningAmberIcon color="error" fontSize="small"/>
                    )}
                </Box>
            )}
        </Box>

    );
}