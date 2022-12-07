import { KcContext } from "lib/kc"
import { useState } from "react"

export type KcContext_UpdatePassword = Extract<KcContext, { pageId: "login-update-password.ftl" }>

type UpdatePasswordForm = {
    password: string,
    passwordError: string | undefined,
    onPasswordChange: (value: string) => void,
    passwordConfirm: string,
    passwordConfirmError: string | undefined,
    onConfirmChange: (value: string) => void,
    onSubmit: () => void
}

export const useUpdatePasswordForm = (kcContext: KcContext_UpdatePassword, onFormSubmit: () => void): UpdatePasswordForm => {
    const [password, setPassword] = useState("");
    const [passwordError, setPasswordError] = useState(extractError(kcContext, "password"));
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [passwordConfirmError, setPasswordConfirmError] = useState(extractError(kcContext, "password-confirm"));

    return {
        password,
        passwordError,
        onPasswordChange: (value) => {
            setPassword(value);
        },
        passwordConfirm,
        passwordConfirmError,
        onConfirmChange: (value) => {
            setPasswordConfirm(value);
        },
        onSubmit: () => {
            let error = false;
            if (password.length === 0) {
                setPasswordError("error-empty");
                error = true;
            }
            if (passwordConfirm.length === 0) {
                setPasswordConfirmError("error-empty");
                error = true;
            }
            if (password !== passwordConfirm) {
                setPasswordConfirmError("invalidPasswordConfirmMessage");
                error = true;
            }

            if (!error) {
                onFormSubmit();
            }
        }
    };
}

const extractError = (kcContext: KcContext_UpdatePassword, fieldName: string): string | undefined => {
    try {
        if (kcContext.messagesPerField.exists(fieldName) && kcContext.messagesPerField.existsError(fieldName)) {
            return kcContext.messagesPerField.get(fieldName);
        }
    } catch (error) {
        return undefined;
    }
    return undefined;
}   
