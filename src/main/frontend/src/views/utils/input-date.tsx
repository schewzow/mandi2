import '../../assets/css/modal.css'
import {useState} from "react";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import dayjs, {Dayjs} from 'dayjs';
import {toDateStringY} from "../../utils/date.ts";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import 'dayjs/locale/de';
import {Box} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";

export const InputDate: React.FC<{
    value: string,
    name: string,
    label: string,
    showSaveAcknowledge: boolean,
    onSave: (value: string) => void,
}> = (props) => {

    const [value, setValue] = useState<Dayjs | null>(dayjs(toDateStringY(props.value)));
    const [showSaveAck, setShowSaveAck] = useState<boolean>(false);

    const saveChanges = (value: Dayjs | null) => {

        if (props.showSaveAcknowledge) {
            setShowSaveAck(true);
        }

        if (value !== null && value !== undefined) {
            props.onSave(value.format('YYYY-MM-DD'));
        }

        setValue(value);

        if (props.showSaveAcknowledge) {
            setTimeout(() => {
                setShowSaveAck(false);
            }, 300);
        }
    }

    return (
        <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="de">
                <DatePicker
                    label={props.label}
                    slotProps={{textField: {size: 'small'}}}
                    value={value}
                    onChange={(newValue) => saveChanges(newValue)}
                />
            </LocalizationProvider>

            {props.showSaveAcknowledge ?
                <CheckCircleIcon
                    color='success'
                    fontSize="small"
                    sx={{
                        marginLeft: 1,
                        visibility: showSaveAck ? 'visible' : 'hidden',
                    }}
                />
                :
                <></>
            }
        </Box>
    );
};
