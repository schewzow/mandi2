import {styled, TextField, type TextFieldProps} from "@mui/material";


export const StyledTextField =
    styled(props => <TextField {...props} />)<TextFieldProps>`

    & input
    {
        color: ${props => props.onClick ? "#FFA500" : (props.disabled ? "rgba(0, 0, 0, 0.38)" : "#737373")} !important;
        ${props => props.onClick && `
            cursor: pointer !important;
            &:hover
            {
                color: #505050 !important;
            }
        `}
    }

    & > label
    {
        color: #969696;
    }
`;


