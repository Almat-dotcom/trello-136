import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { useRef, useState, memo } from "react";

type KcContext_UpdatePassword = Extract<KcContext, { pageId: "login-update-password.ftl" }>;

const UpdatePassword = memo((props: PageProps<KcContext_UpdatePassword, I18n>) => {
    const { kcContext, i18n, Template, ...kcProps } = props;
    const { url, message,username } = kcContext;
    const {  msgStr, advancedMsgStr } = i18n;

    const [password, setPassword] = useState("");
    const [passwordError, setPasswordError] = useState(getError(kcContext, "password"));
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [passwordConfirmError, setPasswordConfirmError] = useState(getError(kcContext, "password-confirm"));

    const formRef = useRef<HTMLFormElement>(null);

    const onSubmit = () => {
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
            formRef.current?.submit();
        }
    };

    return (
        <div className="afr-login">
            <div id="kc-header" className="afr-header">
                <div id="kc-header-wrapper" className="afr-header-wrapper">
                    <span>AFR</span>
                </div>
            </div>
            <div className="afr-card">
                <header className="">
                    <h2 className="text-2xl mx-auto font-bold text-slate-900">
                        {msgStr("updatePasswordTitle")}
                    </h2>
                    {message && (
                        <div className={`afr-message-container afr-message-container-${message.type}`}>
                            <div className={`afr-message afr-message-${message.type}`}>
                                {advancedMsgStr(message.summary)}
                            </div>
                        </div>
                    )}
                </header>
                <div className="afr-content mt-4">
                    <form id="kc-update-password" ref={formRef} action={url.loginAction} method="post">
                        <input
                            className="hidden"
                            id="username"
                             name="username"
                            type="hidden"
                            readOnly
                            value={username}
                            onChange={() => { }}
                        />
                        <div className={clsx(kcProps.kcFormGroupClass)}>
                            <label htmlFor="password" className={clsx(kcProps.kcLabelClass)}>
                                {msgStr("passwordNew")}
                            </label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                className={clsx(kcProps.kcInputClass)}
                                value={password}
                                required
                                onChange={(e) => {
                                    setPassword(e.target.value);
                                    if (e.target.value.length > 0) {
                                        setPasswordError(undefined); // Очищаем ошибку, если поле не пустое
                                    }
                                }}
                            />
                            {passwordError && <p className="text-red-500 text-sm">{advancedMsgStr(passwordError)}</p>}
                        </div>

                        <div className={clsx(kcProps.kcFormGroupClass)}>
                            <label htmlFor="password-confirm" className={clsx(kcProps.kcLabelClass)}>
                                {msgStr("passwordNewConfirm")}
                            </label>
                            <input
                                id="password-confirm"
                                name="passwordConfirm"
                                type="password"
                                className={clsx(kcProps.kcInputClass)}
                                value={passwordConfirm}
                                required
                                onChange={(e) => {
                                    setPasswordConfirm(e.target.value);
                                    if (e.target.value.length > 0) {
                                        setPasswordConfirmError(undefined); // Очищаем ошибку, если поле не пустое
                                    }
                                }}
                            />
                            {passwordConfirmError && <p className="text-red-500 text-sm">{advancedMsgStr(passwordConfirmError)}</p>}
                        </div>

                        <div className="afr-form-buttons">
                            <button type="button" className={clsx(kcProps.kcButtonClass, kcProps.kcButtonPrimaryClass)} onClick={onSubmit}>
                                {msgStr("doSubmit")}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
});

const getError = (kcContext: KcContext_UpdatePassword, field: string): string | undefined => {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch {
        return undefined;
    }
};

export default UpdatePassword;