import { useState } from "react";
import { KcContextLogin } from "../Type";

type UseramePasswordForm = {
    username: string;
    setUsername: (value: string) => void;
    password: string;
    setPassword: (value: string) => void;
    getError: (field: string) => string | undefined;
    onSubmit: () => void;
};

export const useUsernamePasswordForm = (
    kcContext: KcContextLogin,
    variant: "iin" | "email",
    onFormSubmit: () => void
): UseramePasswordForm => {
    const { login, messagesPerField } = kcContext;

    const extractError = (name: string) => {
        if (messagesPerField.exists(name) && messagesPerField.existsError(name)) {
            return messagesPerField.get(name);
        }
        return undefined;
    };

    const [username, setUsername] = useState(login.username ?? "");
    const [password, setPassword] = useState("");
    const [usernameError, setUsernameError] = useState(
        extractError("username")
    );
    const [passwordError, setPasswordError] = useState(
        extractError("password")
    );

    /* добавляем суффикс -physical при отправке формы */
    const normalizeUsername = (value: string): string => {
        if (variant === "iin" && /^\d{12}$/.test(value)) {
            return `${value}-physical`;
        }
        return value;
    };

    const validateForm = () => {
        let error = false;

        if (username.length === 0) {
            setUsernameError("error-empty");
            error = true;
        } else if (variant === "iin" && (username.length !== 12 || isNaN(+username))) {
            setUsernameError("only12Digits");
            error = true;
        }

        if (password.length === 0) {
            setPasswordError("error-empty");
            error = true;
        }

        if (!error) {
            /* отправляем нормализованное имя */
            const form = document.getElementById("kc-form-login") as HTMLFormElement | null;
            if (form) {
                const hidden = document.createElement("input");
                hidden.type = "hidden";
                hidden.name = "username";
                hidden.value = normalizeUsername(username);
                form.appendChild(hidden);
            }
            onFormSubmit();
        }
    };

    return {
        username,
        setUsername: (val) => {
            if (usernameError) setUsernameError(undefined);
            setUsername(val);
        },
        password,
        setPassword: (val) => {
            if (passwordError) setPasswordError(undefined);
            setPassword(val);
        },
        getError: (field) =>
            field === "username" ? usernameError : field === "password" ? passwordError : undefined,
        onSubmit: validateForm,
    };
};
