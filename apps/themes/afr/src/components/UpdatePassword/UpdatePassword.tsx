import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { useRef, useState, memo } from "react";
import emblem from "../KcApp/emblem.png";

type KcContext_UpdatePassword = Extract<KcContext, { pageId: "login-update-password.ftl" }>;

const UpdatePassword = memo((props: PageProps<KcContext_UpdatePassword, I18n>) => {
    const { kcContext, i18n, Template, ...kcProps } = props;
    const { url, message,username,locale } = kcContext;
    const {  msgStr, advancedMsg, advancedMsgStr } = i18n;

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

    const { currentLanguageTag, supported } = locale!;
    const languages = supported
        .map(it =>
            <li key={it.languageTag} className="kc-dropdown-item"><a href={it.url}>{advancedMsg(it.languageTag)}</a></li>
        );

    const currentLang = supported.find(it => it.languageTag === currentLanguageTag);

    return (
        <div className="afr-login">
            <div id="kc-header" className="afr-header">
            <div className="afr-header-content">
                    <img src={emblem} alt="Lock Icon" height="100px" />
                    <p className="header-text">{msgStr("afrHeader")}</p>
                </div>
            </div>
            <div className="afr-card">
                <header className="">
                <div id="kc-locale">
                <div id="kc-locale-wrapper" className="">
                    <div className="kc-dropdown" id="kc-locale-dropdown">
                        <a href={currentLang!.url} id="kc-current-locale-link">{advancedMsg(currentLang!.label)}</a>
                        <ul>
                            {languages}
                        </ul>
                    </div>
                </div>
            </div>
                </header>
                <div className="afr-content mt-4">
                <h2 className="text-2xl mx-auto font-bold text-slate-900"style={{ marginTop:"40px", textAlign: "center"}}>
                        {msgStr("updatePasswordTitle")}
                    </h2>
                    {message && (
                        <div className={`afr-message-container afr-message-container-${message.type}`}>
                            <div className={`afr-message afr-message-${message.type}`}>
                                {advancedMsgStr(message.summary)}
                            </div>
                        </div>
                    )}
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
                            <label htmlFor="password" >
                                {msgStr("passwordNew")}
                            </label>
                            <input
                                id="password"
                                name="password-new"
                                type="password"
                                value={password}
                                required
                                onChange={(e) => {
                                    setPassword(e.target.value);
                                    if (e.target.value.length > 0) {
                                        setPasswordError(undefined); // Очищаем ошибку, если поле не пустое
                                    }
                                }}
                                style={{
                                    width: "90%",
                                    marginTop:"3%",
                                    padding: "0.75rem",
                                }}
                            />
                            {passwordError && <p className="text-red-500 text-sm">{advancedMsgStr(passwordError)}</p>}
                        </div>

                        <div className={clsx(kcProps.kcFormGroupClass)} style={{marginTop:"3%"}}>
                            <label htmlFor="password-confirm">
                                {msgStr("passwordNewConfirm")}
                            </label>
                            <input
                                id="password-confirm"
                                name="password-confirm"
                                type="password"
                                value={passwordConfirm}
                                required
                                onChange={(e) => {
                                    setPasswordConfirm(e.target.value);
                                    if (e.target.value.length > 0) {
                                        setPasswordConfirmError(undefined); // Очищаем ошибку, если поле не пустое
                                    }
                                }}
                                style={{
                                    width: "90%",
                                    marginTop:"3%",
                                    padding: "0.75rem",
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