import '../../assets/css/modal.css'
import {useState} from "react";
import {InputAdornment, TextField} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
//import {useCallback, useEffect} from "react";

export const InputTextArea: React.FC<{
    value: string,
    name: string,
    label: string,
    showSaveAcknowledge: boolean,
    maxLength?: number,
    rows: number,
    onSave: (value: string) => void,
}> = (props) => {

    const [savedValue, setSavedValue] = useState<string>(props.value);
    const [currentValue, setCurrentValue] = useState<string>(props.value);
    const [validationError, setValidationError] = useState<string>("");
    const [showSaveAck, setShowSaveAck] = useState<boolean>(false);

    const valueChangeHandler = (value: string) => {
        setCurrentValue(value);
        // check validation - if invalid show validation error
        if (props.maxLength && value.length > props.maxLength) {
            setValidationError("Maximum length is " + props.maxLength);
        } else {
            setValidationError("");
        }
    }

    const saveChanges = () => {
        if (validationError && validationError.length > 0) {
            return;
        }
        if (savedValue === currentValue) {
            // no changes
            return;
        }

        if (props.showSaveAcknowledge) {
            setShowSaveAck(true);
        }

        props.onSave(currentValue);
        setSavedValue(currentValue);

        if (props.showSaveAcknowledge) {
            setTimeout(() => {
                setShowSaveAck(false);
            }, 300);
        }
    }

    return (
        <TextField
            fullWidth
            error={validationError!==""? true: false}
            helperText={validationError}
            id={props.name}
            label={props.label}
            multiline
            rows={props.rows}
            slotProps={{
                input: {
                    //endAdornment: <CheckCircleIcon fontSize="small" />,
                    endAdornment: (
                        <InputAdornment position="end">
                            {showSaveAck ?
                                <CheckCircleIcon color='success' fontSize="small"/>
                                : <></>
                            }
                        </InputAdornment>
                    ),
                },
            }}
            variant="standard"
            value={currentValue}
            onChange={(e) => {
                valueChangeHandler(e.target.value);
            }}
            onBlur={() => {
                saveChanges();
            }}
            onKeyUp={(e) => {
                if (e.key === 'Enter') {
                    saveChanges();
                }
            }}
        />
    )
        ;
};
