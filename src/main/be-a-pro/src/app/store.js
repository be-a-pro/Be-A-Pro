import { configureStore } from "@reduxjs/toolkit";
import socialCheckReducer from "../features/socialCheck";

export default configureStore({
    reducer: {
        opener: socialCheckReducer
    },
})