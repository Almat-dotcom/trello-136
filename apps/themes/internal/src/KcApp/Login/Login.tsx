import { memo } from "react";
import type { KcProps } from "keycloakify";
import type { KcContext } from "KcApp/kcContext";
import type { I18n } from "KcApp/i18n";
import { clsx } from "keycloakify/lib/tools/clsx";

type KcContext_Login = Extract<KcContext, { pageId: "login.ftl" }>;

const Login = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Login; i18n: I18n; } & KcProps) => {
    const {url, messagesPerField, login, realm, auth} = kcContext
    const {msg, msgStr} = i18n

    return (
        <div className={clsx(props.kcLoginClass)}>
            <div className={clsx(props.kcHeaderClass)}>
                <div className={clsx(props.kcHeaderWrapperClass)}>{realm.displayName || realm.name}</div>
            </div>
            <form id="kc-login-form" className={clsx(props.kcFormCardClass)} action={url.loginAction} method="post">
                <div className={clsx(props.kcFormGroupClass, messagesPerField.printIfExists("username", props.kcFormGroupErrorClass))}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="username" className={clsx(props.kcLabelClass)}>
                            {msg("username")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input
                            type="text"
                            id="username"
                            className={clsx(props.kcInputClass)}
                            name="username"
                            defaultValue={login.username ?? ""}
                        />
                    </div>
                </div>
                <div className={clsx(props.kcFormGroupClass, messagesPerField.printIfExists("password", props.kcFormGroupErrorClass))}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="password" className={clsx(props.kcLabelClass)}>
                            {msg("password")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input
                            type="password"
                            id="password"
                            className={clsx(props.kcInputClass)}
                            name="password"
                        />
                    </div>
                </div>
                <div>
                    {realm.rememberMe && (
                        <>
                            <input type="checkbox" id="rememberMe" name="rememberMe" defaultChecked={!!login.rememberMe} />
                            <label htmlFor="rememberMe">{msgStr("rememberMe")}</label>
                        </>
                    )}
                </div>
                <div>
                    <input
                        type="hidden"
                        name="credentialId"
                        {...(auth?.selectedCredential !== undefined ? {"value": auth.selectedCredential}: {})}
                    />
                    <button name="login" type="submit">{msgStr("doLogIn")}</button>
                </div>
            </form>
        </div>
    )
})

export default Login
