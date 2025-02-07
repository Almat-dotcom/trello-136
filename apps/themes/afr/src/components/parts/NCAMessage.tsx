import React, { memo } from "react";
import { I18n } from "lib/i18n";


interface NCAMessageProps {
    message: string;
    i18n: I18n;
}

export const NCAMessage = memo(({ message, i18n }: NCAMessageProps) => {
    const dark = (msgType: string) => {
        switch (msgType) {
            case "ncaSignProgress":
                return "#8b8d8f";
            case "ncaSignFinished":
                return "#3f9c35";
            case "ncaCancelled":
                return "#ec7a08";
            default:
                return "#cc0000";
        }
    };
    const light = (msgType: string) => {
        switch (msgType) {
            case "ncaSignInProgress":
                return "#f5f5f5";
            case "ncaSignFinished":
                return "#e9f4e9";
            case "ncaCancelled":
                return "#fdf2e5";
            default:
                return "#ffffff";
        }
    };

    if (!message) {
        return null;
    }

    return (
        <div
            style={{
                color: dark(message),
                backgroundColor: light(message),
                border: `2px solid ${dark(message)}`,
                borderRadius: "5px",
                padding: "1rem",
                fontSize: "1rem"
            }}
        >
            {i18n.advancedMsgStr(message)}
        </div>
    );
});
