import '../../assets/css/modal.css'
import {useState} from "react";
import {Box, FormControlLabel, Switch} from "@mui/material";
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
//import {useCallback, useEffect} from "react";

export const InputSwitch: React.FC<{
    checked: boolean,
    name: string,
    label: string,
    showSaveAcknowledge: boolean,
    onSave: (checked: boolean) => void,
}> = (props) => {

    const [checked, setChecked] = useState<boolean>(props.checked);
    const [showSaveAck, setShowSaveAck] = useState<boolean>(false);

    const saveChanges = (check: boolean) => {

        if (props.showSaveAcknowledge) {
            setShowSaveAck(true);
        }

        props.onSave(check);

        setChecked(check);

        if (props.showSaveAcknowledge) {
            setTimeout(() => {
                setShowSaveAck(false);
            }, 300);
        }
    }

    return (
        <Box sx={{alignItems: 'center', display: 'flex', flexDirection: 'row', justifyItems: "start"}}>
            <FormControlLabel
                control={<Switch
                    checked={checked}
                    onChange={(e) => saveChanges(e.target.checked)}
                    inputProps={{'aria-label': 'controlled'}}
                />} label={props.label}/>
            {showSaveAck ?
                <CheckCircleIcon fontSize="small" color='success'/>
                :
                <></>
            }
        </Box>
    )
        ;
};
