import '../../assets/css/modal.css'
import {useState} from "react";
import {InputAdornment, TextField} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
//import {useCallback, useEffect} from "react";

export const InputNumber: React.FC<{
    value: number,
    name: string,
    label: string,
    showSaveAcknowledge: boolean,
    onSave: (value: number) => void,
}> = (props) => {

    const [savedValue, setSavedValue] = useState<number>(props.value);
    const [currentValue, setCurrentValue] = useState<string>(props.value.toString());
    const [validationError, setValidationError] = useState<string>("");
    const [showSaveAck, setShowSaveAck] = useState<boolean>(false);

    const valueChangeHandler = (value: string) => {
        setCurrentValue(value);

        // check validation - if invalid show validation error
        const _value = value.replace(',', '.');
        if (!_value || isNaN(+_value)) {
            setValidationError("Please enter numeric value");
        }
        else {
            setValidationError("");
        }
    }

    const saveChanges = () => {
        if(validationError && validationError.length > 0) {
            return;
        }

        if(savedValue.toString() === currentValue) {
            // no changes
            return;
        }

        if (props.showSaveAcknowledge) {
            setShowSaveAck(true);
        }

        const _value = parseFloat(currentValue.replace(',', '.'));
        props.onSave(_value);
        setSavedValue(_value);

        if (props.showSaveAcknowledge) {
            setTimeout(() => {
                setShowSaveAck(false);
            }, 300);
        }
    }

    return (
        <TextField
            //sx={{input: {color:"red"}}}
            id={props.name}
            label={props.label}
            variant="standard"
            fullWidth
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
            error={validationError!==""? true: false}
            helperText={validationError}
        />
    )
        ;
};
