import {Box, Button, TextField, Typography} from "@mui/material";
import {useState} from "react";
import {useNavigate} from "react-router";
import {StyledLayoutWrapper} from "../utils/styled-layout-wrapper.tsx";
import {type LoginPayload, loginUser} from "../../store/reducers/authSlice.ts";
import {useAppDispatch} from "../../store/hooks/hooks.ts";

export const LogIn = () => {

    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    async function handleLogin() {
        const sendData: LoginPayload = {
            username: username,
            password: password,
        }
        //const {data} = await api.post("/auth/signin", sendData);
        dispatch(loginUser(sendData, navigate));
    }

    return (
        <StyledLayoutWrapper>
            <Box justifyItems="center" marginTop={8}>
                <Box sx={{
                    width: 280,
                    padding: 2,
                    borderRadius: 1,
                    bgcolor: (theme) => theme.palette.background.paper,
                    marginBottom: 2,
                }} justifyItems={'center'}>
                    <Typography>
                        Login
                    </Typography>
                    <form>
                        <TextField
                            sx={{marginTop: 4}}
                            fullWidth={true}
                            label="User"
                            id="labelUser"
                            variant="standard"
                            size="small"
                            type="text"
                            value={username}
                            autoComplete="username"
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <TextField
                            fullWidth={true}
                            sx={{marginTop: 2}}
                            label="Password"
                            id="labelPassword"
                            variant="standard"
                            size="small"
                            type="password"
                            value={password}
                            autoComplete="current-password"
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <Button
                            variant="contained"
                            fullWidth={true}
                            sx={{marginTop: 6}}
                            onClick={handleLogin}
                        >
                            Login
                        </Button>
                    </form>
                </Box>
            </Box>
        </StyledLayoutWrapper>
    )
        ;
}