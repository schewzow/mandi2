import {Typography} from "@mui/material";
import {StyledLayoutWrapper} from "../utils/styled-layout-wrapper.tsx";
import {FormattedMessage} from "react-intl";

export const HomePage = () => {

    return (
        <StyledLayoutWrapper>
            <Typography variant="h6" color='success'>This is homepage</Typography>
            <Typography variant="h6" color='success'>
                <FormattedMessage id="language.german" />
            </Typography>
        </StyledLayoutWrapper>
    )
        ;
}