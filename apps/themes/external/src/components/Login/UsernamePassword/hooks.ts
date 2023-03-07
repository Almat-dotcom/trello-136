import { useState } from "react";
import { KcContextLogin } from "../Type";

type UseramePasswordForm = {
    username: string,
    setUsername: (value: string) => void,
    password: string,
    setPassword: (value: string) => void,
    getError: (field: string) => string | undefined,
    onSubmit: () => void
}

const useUsernamePasswordForm = (kcContext: KcContextLogin, variant: "iin" | "email", onFormSubmit: () => void): UseramePasswordForm => {
    const { login, messagesPerField } = kcContext;

    const extractError = (name: string) => {
        if (messagesPerField.exists(name) && messagesPerField.existsError(name)) {
            return messagesPerField.get(name);
        }
        return undefined;
    }

    const [username, setUsername] = useState(login.username ?? "");
    const [password, setPassword] = useState("");
    const [usernameError, setUsernameError] = useState(extractError("username"));
    const [passwordError, setPasswordError] = useState(extractError("password"));

    const validateForm = () => {
        let error = false;
        if (username.length === 0) {
            setUsernameError("error-empty");
            error = true;
        } else if (variant === "iin" && username.length !== 12 && !isNaN(+username)) {
            setUsernameError("only12Digits");
            error = true;
        }
        if (password.length === 0) {
            setPasswordError("error-empty");
            error = true;
        }
        if (!error) {
            onFormSubmit();
        }
    }

    return {
        username: username,
        setUsername: (value) => {
            if (usernameError) {
                setUsernameError(undefined);
            }
            setUsername(value);
        },
        password: password,
        setPassword: (value) => {
            if (passwordError) {
                setPasswordError(undefined);
            }
            setPassword(value);
        },
        getError: (field) => {
            if (field === "username") {
                return usernameError;
            }
            if (field === "password") {
                return passwordError;
            }
            return undefined;
        },
        onSubmit: () => {
            validateForm()
        }
    }
}

export { useUsernamePasswordForm }
