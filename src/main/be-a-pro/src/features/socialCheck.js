import { createAsyncThunk, createSlice, isFulfilled } from "@reduxjs/toolkit";

export const socialCheck = createSlice({
    name: "socialCheck",
    initialState: {
        state: false,
    },
    reducers: {
        open: (state) => {
            state.state = true;
        },
        close: (state) => {
            state.state = false;
        },
    }
})

export const { open, close } = socialCheck.actions;
export default socialCheck.reducer;