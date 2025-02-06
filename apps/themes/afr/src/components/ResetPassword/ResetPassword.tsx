import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { ChangeEvent, useRef, useState, memo } from "react";
import emblem from "../KcApp/emblem.png";

type KcContext_ResetPassword = Extract<KcContext, { pageId: "login-reset-password.ftl" }>;

const ResetPassword = memo((props: PageProps<KcContext_ResetPassword, I18n>) => {
    const { kcContext, i18n, Template, ...kcProps } = props;
    const { url, message, auth, locale } = kcContext;
    const { msg, msgStr,advancedMsg, advancedMsgStr } = i18n;

    const [username, setUsername] = useState(auth?.attemptedUsername ?? "");
    const [error, setError] = useState(getError(kcContext, "username") ?? undefined);

    const formRef = useRef<HTMLFormElement>(null);

    const onUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
    };

    const onSubmit = () => {
        if (username.length === 0) {
            setError("error-empty");
            return;
        }
        formRef.current?.submit();
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
        <header className="text-center">
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
        <div className="afr-content">
        
            <h2 className="font-bold text-slate-900 text-center mb-4" style={{ marginTop:"40px", textAlign: "center"}}>{msgStr("emailForgotTitle")}</h2>
            {message && (
                <div className={`afr-message-container afr-message-container-${message.type}`} style={{ marginTop:"1px"}}>
                    <div className={`afr-message afr-message-${message.type}`}>
                        {advancedMsgStr(message.summary)}
                    </div>
                </div>
            )}
            <form id="kc-reset-password-form" ref={formRef} action={url.loginAction} method="post" style={{marginTop:"5%"  }}>
                <div className={clsx(kcProps.kcFormGroupClass)} style={{ marginBottom: "1rem" }}>
                    <label htmlFor="username">
                        {msgStr("usernameOrEmail")}
                    </label>
                    <input
                        id="username"
                        name="username"
                        type="text"
                        value={username}
                        required
                        onChange={onUsernameChange}
                        style={{
                            width: "90%",
                            marginTop:"3%",
                            padding: "0.75rem",
                        }}
                    />
                    {error && <p className="text-red-500 text-sm">{advancedMsgStr(error) ?? error}</p>}
                </div>
                <p className="text-sm text-center text-gray-400 mt-4">
                    <a href={url.loginUrl} className="text-secondary-dark font-semibold underline">
                        {msg("backToLogin")}
                    </a>
                </p>
                <div className="afr-form-buttons text-center">
                    <button type="button" className={clsx(kcProps.kcButtonClass, kcProps.kcButtonPrimaryClass)} onClick={onSubmit} style={{ padding: "0.5rem 1rem", borderRadius: "8px" }}>
                        {msgStr("doSubmit")}
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

)});

const getError = (kcContext: KcContext_ResetPassword, field: string): string | undefined => {
    try {
        return kcContext.messagesPerField.printIfExists(field, undefined);
    } catch {
        return undefined;
    }
};

export default ResetPassword;