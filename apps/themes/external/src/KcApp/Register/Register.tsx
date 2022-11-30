import { ChangeEvent, memo, useState } from "react";
import type { KcProps } from "keycloakify";
import type { KcContext } from "KcApp/kcContext";
import type { I18n } from "KcApp/i18n";
import { clsx } from "keycloakify/lib/tools/clsx";

type KcContext_Registration = Extract<KcContext, { pageId: "register.ftl" }>;

const Registration = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Registration; i18n: I18n; } & KcProps) => {
    const { url, realm } = kcContext;
    const { msg, msgStr } = i18n;

    const [clientType, setClientType] = useState("");
    const onClientTypeChanged = (event: ChangeEvent<HTMLSelectElement>) => {
        setClientType(event.target.value)
    };

    const [legalRole, setLegalRole] = useState("");
    const onLegalRoleChanged = (event: ChangeEvent<HTMLSelectElement>) => {
        setLegalRole(event.target.value)
    }

    return (
        <div className={clsx(props.kcLoginClass)}>
            <div className={clsx(props.kcHeaderClass)}>
                <div className={clsx(props.kcHeaderWrapperClass)}>{realm.displayName || realm.name}</div>
            </div>
            <form id="kc-register-form" className={clsx(props.kcFormCardClass)} action={url.registrationAction} method="post">
                <fieldset className={clsx(props.kcFormHeaderClass)}>{msgStr("registerTitle")}</fieldset>
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="clientType" className={clsx(props.kcLabelClass)}>
                            {msg("clientType")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <select
                            id="clientType"
                            name="clientType"
                            className={clsx(props.kcInputClass)}
                            defaultValue={clientType}
                            onChange={onClientTypeChanged}
                        >
                            <option value="" disabled>{msgStr("doCheckClientType")}</option>
                            <option value="physical">{msgStr("physical")}</option>
                            <option value="legal">{msgStr("legal")}</option>
                        </select>
                    </div>
                </div>
                { clientType === "legal" ? (
                    <div className={clsx(props.kcFormGroupClass)}>
                        <div className={clsx(props.kcLabelWrapperClass)}>
                            <label htmlFor="legalRole" className={clsx(props.kcLabelClass)}>
                                {msg("legalRole")}
                            </label>
                        </div>
                        <div className={clsx(props.kcInputWrapperClass)}>
                            <select
                                id="legalRole"
                                name="legalRole"
                                className={clsx(props.kcInputClass)}
                                defaultValue={legalRole}
                                onChange={onLegalRoleChanged}
                            >
                                <option value="" disabled>{msgStr("doCheckRole")}</option>
                                <option value="physical">{msgStr("head")}</option>
                                <option value="legal">{msgStr("employee")}</option>
                            </select>
                        </div>
                    </div>
                ) : null}
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="lastName" className={clsx(props.kcLabelClass)}>
                            {msg("lastName")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="lastName" name="lastName" type="text" className={clsx(props.kcInputClass)} placeholder={msgStr("doEnterLastName")}/>
                    </div>
                </div>
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="firstName" className={clsx(props.kcLabelClass)}>
                            {msg("firstName")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="firstName" name="firstName" type="text" className={clsx(props.kcInputClass)} placeholder={msgStr("doEnterFirstName")}/>
                    </div>
                </div>
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="middleName" className={clsx(props.kcLabelClass)}>
                            {msg("middleName")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="middleName" name="middleName" type="text" className={clsx(props.kcInputClass)} placeholder={msgStr("doEnterMiddleName")}/>
                    </div>
                </div>
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="email" className={clsx(props.kcLabelClass)}>
                            {msg("email")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="email" name="email" type="email" className={clsx(props.kcInputClass)} placeholder={msgStr("doEnterEmail")}/>
                    </div>
                </div>
                {clientType === "legal" ? (
                    <div className={clsx(props.kcFormGroupClass)}>
                        <div className={clsx(props.kcLabelWrapperClass)}>
                            <label htmlFor="bin" className={clsx(props.kcLabelClass)}>
                                {msg("bin")}
                            </label>
                        </div>
                        <div className={clsx(props.kcInputWrapperClass)}>
                            <input id="bin" name="bin" type="text" className={clsx(props.kcInputClass)} placeholder={msgStr("doEnterBin")}/>
                        </div>
                    </div>
                ) : null}
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="iin" className={clsx(props.kcLabelClass)}>
                            {msg("iin")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="iin" name="iin" type="text" className={clsx(props.kcInputClass)} placeholder={msgStr("doEnterIin")}/>
                    </div>
                </div>
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="password" className={clsx(props.kcLabelClass)}>
                            {msg("password")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="password" name="password" type="password" className={clsx(props.kcInputClass)} placeholder="*****************"/>
                    </div>
                </div>
                <div className={clsx(props.kcFormGroupClass)}>
                    <div className={clsx(props.kcLabelWrapperClass)}>
                        <label htmlFor="password-confirm" className={clsx(props.kcLabelClass)}>
                            {msg("passwordConfirm")}
                        </label>
                    </div>
                    <div className={clsx(props.kcInputWrapperClass)}>
                        <input id="password-confirm" name="password-confirm" type="password" className={clsx(props.kcInputClass)} placeholder="*****************"/>
                    </div>
                </div>
                <div className={clsx(props.kcFormButtonsClass)}>
                    <button id="doRegister" className={clsx(props.kcButtonPrimaryClass)} type="submit">{msgStr("doRegister")}</button>
                </div>
                <div className={clsx(props.kcFormOptionsWrapperClass)}>
                    <a href={url.loginUrl}>{msg("backToLogin")}</a>
                </div>
            </form>
        </div>
    )
})

export default Registration
