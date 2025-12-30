import {
    Box,
    FormControl,
    FormControlLabel,
    FormGroup,
    FormHelperText,
    Stack,
    Switch,
    type SwitchProps
} from "@mui/material";
import {useEntityField} from "../../../hooks/useEntityField.ts";
import type {BaseEntity} from "../../../types/base-entity.ts";
import type {StringKeys, StringValue} from "../field-utils";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import {useSaveAcknowledgment} from "../../../hooks/useSaveAcknowledgment.ts";
import {FormattedMessage} from "react-intl";
import type {EntityFieldProps} from "../EntityFieldProps.ts";

export interface EntitySwitchFieldProps<
    T extends BaseEntity,
    K extends StringKeys<T>
> extends EntityFieldProps<T, K> {
    /** MUI props passthrough */
    fieldProps?: Omit<SwitchProps, "value" | "onChange" | "error" | "helperText">;
};

export function EntitySwitchField<T extends BaseEntity, K extends StringKeys<T>>(props: EntitySwitchFieldProps<T, K>) {
    const {
        data,
        errors,
        states,
        setField,
        field,
        label,
        loading,
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
        loading,
    });

    const showAck = useSaveAcknowledgment(state, showAcknowledge);

    function handleOnChange(value: boolean) {
        const nextValue = value ? "true" : "false";
        onChange(nextValue as StringValue<T, K>);
    }

    return (
        <Stack direction="row" spacing={1}>
            <FormControl component="fieldset" variant="standard">
                <FormGroup>
                    <FormControlLabel
                        control={
                            <Switch
                                {...fieldProps}
                                checked={Boolean(value)}
                                onChange={(e) => handleOnChange(e.target.checked)}
                                disabled={disabled}
                            />
                        }
                        label={i18nLabel ? <FormattedMessage id={i18nLabel}/> : label}
                    />
                </FormGroup>
                <FormHelperText error={Boolean(error)}>{helperText}</FormHelperText>
            </FormControl>
            <Box
                //sx={{writingMode:"vertical-lr"}}
            >
                {showAck ? (
                    state === "SUCCESS" ? (
                        <CheckCircleIcon color="success" fontSize="small" sx={{marginTop: 1}}/>
                    ) : (
                        <WarningAmberIcon color="error" fontSize="small" sx={{marginTop: 1}}/>
                    )
                ) : undefined}
            </Box>
        </Stack>
    );
}