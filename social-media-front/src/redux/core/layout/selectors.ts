import {RootState} from "../../store";

export const layoutSelect = ({
    isDark: (state: RootState) => state.layout.isDark,
    isModalOpen: (state: RootState) => state.layout.isModalOpen,
    isProfileOpen: (state: RootState) => state.layout.isProfileOpen
});