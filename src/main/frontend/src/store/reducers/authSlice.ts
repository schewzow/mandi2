import type {UserAuth} from "../UserAuth.ts";
import {createSlice, type PayloadAction} from "@reduxjs/toolkit";
import type {NavigateFunction} from "react-router";
import api from "../../api/api.ts";
import type {AppDispatch} from "./Store.ts";

interface AuthState {
    user: UserAuth | null;
}

export interface LoginPayload {
    username: string;
    password: string;
}

async function refreshUser() {
    //console.log("refreshUser called");
    try {
        const refreshedUser = await api.get("/auth/user")
        //console.log("refreshUser response", refreshedUser);
        if (refreshedUser.status === 200 && refreshedUser.data && refreshedUser.data.uuid) {
            return refreshedUser.data;
        }
    } catch (error) {
        console.log("refreshUser error", error);
    }
    return null;
}

const user = await refreshUser();

const initialState: AuthState = {
    user: user,
}

export const authSlice = createSlice({
    name: 'auth',
    // `createSlice` will infer the state type from the `initialState` argument
    initialState,
    reducers: {
        login: (state, action: PayloadAction<UserAuth>) => {
            //console.log(action.payload);
            state.user = action.payload;
        },
        logout: (state) => {
            state.user = null;
        },
    },
})

export function loginUser(sendData: LoginPayload, navigate: NavigateFunction) {
    // fetchTodoByIdThunk is the "thunk function"
    return async function loginUserThunk(dispatch: AppDispatch) {
        // await api.post("/auth/signin", sendData)
        //     .then(response => {
        //         dispatch(login(response.data));
        //         navigate("/");
        //     })
        //     .catch(error => {
        //         //console.log("error in authslice: ", error);
        //     });

        try {
            //setLoader(true);
            const {data} = await api.post("/auth/signin", sendData);
            dispatch(login(data));
            //localStorage.setItem("auth", JSON.stringify(data));
            //reset();
            //toast.success("Login Success");
            navigate("/");
        } catch {
            //console.log("error in authslice: ", error);
            //toast.error(error?.response?.data?.message || "Internal Server Error");
        } finally {
            //setLoader(false);
        }
    }
}

export function logoutUser() {
    // fetchTodoByIdThunk is the "thunk function"
    return async function logoutUserThunk(dispatch: AppDispatch) {
        await api.post("/auth/signout");
        dispatch(logout());
    }
}

export function logoutUserAndNavigate(navigate: NavigateFunction) {
    // fetchTodoByIdThunk is the "thunk function"
    return async function logoutUserThunk(dispatch: AppDispatch) {
        await api.post("/auth/signout");
        dispatch(logout());
        navigate("/login");
    }
}

const {login, logout} = authSlice.actions

export default authSlice.reducer