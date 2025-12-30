import '../../assets/css/modal.css'
import {useState} from "react";
import {InputAdornment, TextField} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
//import {useCallback, useEffect} from "react";

export const InputText: React.FC<{
    value: string,
    name: string,
    label: string,
    showSaveAcknowledge: boolean,
    maxLength?: number,
    onSave: (value: string) => Promise<boolean>,
    error: string,
}> = (props) => {

    const [savedValue, setSavedValue] = useState<string>(props.value);
    const [currentValue, setCurrentValue] = useState<string>(props.value);
    const [validationError, setValidationError] = useState<string>(props.error);
    const [showSaveAck, setShowSaveAck] = useState<boolean>(false);
    const [saveSuccess, setSaveSuccess] = useState<boolean>(true);

    const valueChangeHandler = (value: string) => {
        setCurrentValue(value);
        // check validation - if invalid show validation error
        if (props.maxLength && value.length > props.maxLength) {
            setValidationError("Maximum length is " + props.maxLength);
        } else {
            setValidationError("");
        }
    }

    const saveChanges = async () => {
        if (validationError && validationError.length > 0) {
            return;
        }
        if (savedValue === currentValue) {
            // no changes
            return;
        }

        setSaveSuccess(await props.onSave(currentValue));
        setSavedValue(currentValue);

        if (props.showSaveAcknowledge) {
            setShowSaveAck(true);
            setTimeout(() => {
                setShowSaveAck(false);
            }, 300);
        }
    }

    return (
        <TextField
            fullWidth
            error={props.error !== "" ? true : false}
            //error={validationError!==""? true: false}
            helperText={props.error}
            //helperText={validationError}
            id={props.name}
            label={props.label}
            slotProps={{
                input: {
                    //endAdornment: <CheckCircleIcon fontSize="small" />,
                    endAdornment: (
                        <InputAdornment position="end">
                            {showSaveAck ? saveSuccess ?
                                    <CheckCircleIcon color='success' fontSize="small"/>
                                    :
                                    <CheckCircleIcon color='error' fontSize="small"/>
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
