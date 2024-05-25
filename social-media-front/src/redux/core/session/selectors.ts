import {RootState} from "../../store";

export const sessionSelect = ({
    jwtToken: (state: RootState) => state.session.token,
    id: (state: RootState) => state.session.id,
    email: (state: RootState) => state.session.email,
    username: (state: RootState) => state.session.username,
    fullName: (state: RootState) => state.session.fullName,
    phoneNumber: (state: RootState) => state.session.phoneNumber,
    profilePhoto: (state: RootState) => state.session.profilePhoto
});