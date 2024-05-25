import {RootState} from "../../../redux/store";

export const authSelect = ({
    authError: (state: RootState) => state.auth.error,
    isLogged: (state: RootState) => state.auth.logged
});