import { memo, useRef } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "../../lib/i18n";
import { Layout, LayoutWithCarousel } from "components/Layout";
import { InputField, InputSelect } from "components/parts/Input";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import { KcContext_Registration, useRegisterPage } from "./hooks";
import PdfModal from "components/parts/PdfModal";
import PaRu from "./pa_ru.md";
import PaKz from "./pa_kz.md";
import PaEn from "./pa_en.md";
import LkRu from "./lk_ru.md";
import LkKz from "./lk_kz.md";
import LkEn from "./lk_en.md";
import ButtonChoise from "components/parts/ButtonChoise";

const Registration = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Registration; i18n: I18n; } & KcProps) => {
    const { url, message } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    const formRef = useRef<HTMLFormElement>(null);

    const { fields, legal, resident, head, buttonDisabled, concents, onSubmit } = useRegisterPage(kcContext, () => formRef.current?.submit())
    const setResident = () => {
        fields.residency.onChange("resident");
    }
    const setNonResident = () => {
        fields.residency.onChange("nonResident");
    }
    const resetResidency = () => {
        fields.residency.onChange("");
    }

    const error = (key?: string) => key ? advancedMsgStr(key) ?? key : undefined

    const collectingConsent = (lang?: string) => {
        if (lang === "ru") {
            return PaRu;
        }
        if (lang === "kz") {
            return PaKz;
        }
        return PaEn;
    }

    const privateConsent = (lang?: string) => {
        if (lang === "ru") {
            return LkRu;
        }
        if (lang === "kz") {
            return LkKz;
        }
        return LkEn;
    }

    return (
        <>
            {!fields.residency.value ? (
                <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
                    <div>
                        {message && (
                            <Alert i18n={i18n} type={message.type} message={message.summary} />
                        )}

                        <p className="mt-3 mb-4 text-slate-900 text-3xl font-bold">{msgStr("registerTitle")}</p>
                    </div>
                    <div>
                        <ButtonChoise choises={[{ name: msgStr("resident"), onSelect: setResident }, { name: msgStr("nonResident"), onSelect: setNonResident }]} />
                    </div>
                    <p
                        className="my-6 text-sm text-center text-gray-500"
                    >
                        <span>{msg("backToLogin")}</span>
                        <Button type="link" severity="secondary" href={url.loginUrl}>{msgStr("doLogIn")}</Button>
                    </p>
                </LayoutWithCarousel>
            ) : (
                <Layout kcContext={kcContext} i18n={i18n}>
                    <form ref={formRef} id="kc-register-form" action={url.registrationAction} method="post">
                        <div className="text-center">
                            {message && (
                                <Alert i18n={i18n} type={message.type} message={message.summary} />
                            )}

                            <p className="mt-3 mb-4 text-slate-900 text-3xl font-bold">{msgStr("registerTitle")}</p>
                        </div>

                        <input id="residency" name="residency" type="hidden" value={fields.residency.value}/>
                        
                        <InputSelect
                            fieldName="clientType"
                            label={msgStr("clientType")}
                            options={[{
                                value: "physical",
                                label: msgStr("physical")
                            }, {
                                value: "legal",
                                label: msgStr("legal")
                            }]}
                            value={fields.clientType.value}
                            required
                            error={error(fields.clientType.error)}
                            onValueChange={(option) => { fields.clientType.onChange(option.value) }}
                        />
                        {legal ? (
                            <InputSelect
                                fieldName="legalRole"
                                label={msgStr("legalRole")}
                                options={[{
                                    value: "head",
                                    label: msgStr("head")
                                }, {
                                    value: "employee",
                                    label: msgStr("employee")
                                }]}
                                value={fields.legalRole.value}
                                required
                                error={error(fields.legalRole.error)}
                                onValueChange={(option) => fields.legalRole.onChange(option.value)}
                            />
                        ) : null}
                        <InputField
                            fieldName="lastName"
                            label={msgStr("lastName")}
                            type="text"
                            value={fields.lastName.value}
                            required
                            error={error(fields.lastName.error)}
                            onChange={(event) => fields.lastName.onChange(event.target.value)}
                        />
                        <InputField
                            fieldName="firstName"
                            label={msgStr("firstName")}
                            type="text"
                            value={fields.firstName.value}
                            required
                            error={error(fields.firstName.error)}
                            onChange={(event => { fields.firstName.onChange(event.target.value) })}
                        />
                        <InputField
                            fieldName="middleName"
                            label={msgStr("middleName")}
                            type="text"
                            value={fields.middleName.value}
                            error={error(fields.middleName.error)}
                            onChange={(event) => { fields.middleName.onChange(event.target.value) }}
                        />
                        <InputField
                            fieldName="email"
                            label={msgStr("email")}
                            type="text"
                            value={fields.email.value}
                            required
                            error={error(fields.email.error)}
                            onChange={(event) => { fields.email.onChange(event.target.value) }}
                        />
                        {legal && (resident || !head) ? (
                            <InputField
                                fieldName="bin"
                                label={resident ? msgStr("bin") : msgStr("orgCode")}
                                type="text"
                                value={fields.bin.value}
                                required
                                error={error(fields.bin.error)}
                                maxLength={12}
                                onChange={(event) => fields.bin.onChange(event.target.value)}
                            />
                        ) : null}
                        {resident ? (
                            <InputField
                                fieldName="iin"
                                label={msgStr("iin")}
                                type="text"
                                value={fields.iin.value}
                                required
                                error={error(fields.iin.error)}
                                maxLength={12}
                                onChange={(event) => fields.iin.onChange(event.target.value)}
                            />
                        ) : null}
                        <InputField
                            fieldName="password"
                            label={msgStr("password")}
                            type="password"
                            value={fields.password.value}
                            required
                            error={error(fields.password.error)}
                            onChange={(event) => fields.password.onChange(event.target.value)}
                        />
                        <InputField
                            fieldName="password-confirm"
                            label={msgStr("passwordConfirm")}
                            type="password"
                            value={fields.passwordConfirm.value}
                            required
                            error={error(fields.passwordConfirm.error)}
                            onChange={(event) => fields.passwordConfirm.onChange(event.target.value)}
                        />

                        <PdfModal
                            kcContext={kcContext}
                            url={collectingConsent(kcContext.locale?.currentLanguageTag)}
                            shown={concents.concent1Shown}
                            onClosed={concents.tuggleConcent1}
                        />
                        <div className="flex flex-row items-start justify-start mt-4">
                            <div className="flex items-center h-5">
                                <input
                                    id="contentCollectConsent"
                                    name="contentCollectConsent"
                                    type="checkbox"
                                    checked={concents.concent1}
                                    onChange={concents.onConcent1Clicked}
                                    className="bg-gray-50 border-gray-300 accent-primary  focus:ring-3 focus:ring-primary h-4 w-4 rounded"
                                />
                            </div>
                            <div className="text-sm ml-3">
                                <button type="button" className="bg-none border-none text-start text-sm text-secondary-dark font-semibold underline" onClick={concents.tuggleConcent1}>
                                    {msgStr("concentOnCollectingData")}
                                </button>
                            </div>
                        </div>

                        <PdfModal kcContext={kcContext} url={privateConsent(kcContext.locale?.currentLanguageTag)} shown={concents.concent2Shown} onClosed={concents.tuggleConcent2} />
                        <div className="flex flex-row items-start justify-start mt-4">
                            <div className="flex items-center h-5">
                                <input
                                    id="lkConsent"
                                    name="lkConsent"
                                    type="checkbox"
                                    checked={concents.concent2}
                                    onChange={concents.onConcent2Clicked}
                                    className="bg-gray-50 border-gray-300 accent-primary  focus:ring-3 focus:ring-primary h-4 w-4 rounded"
                                />
                            </div>
                            <div className="text-sm ml-3">
                                <button type="button" className="bg-none border-none text-start text-sm text-secondary-dark font-semibold underline" onClick={concents.tuggleConcent2}>
                                    {msgStr("concentLK")}
                                </button>
                            </div>
                        </div>

                        <div>
                            <Button severity="primary" type="button" disabled={buttonDisabled} onClick={() => onSubmit()}>{msgStr("doRegister")}</Button>
                        </div>

                        <div className="w-min">
                            <Button type="button" severity="link-primary" onClick={resetResidency}>&laquo;{msgStr("doBack")}</Button>
                        </div>

                        <p
                            className="my-6 text-sm text-center text-gray-500"
                        >
                            <span>{msg("backToLogin")}</span>
                            <Button type="link" severity="secondary" href={url.loginUrl}>{msgStr("doLogIn")}</Button>
                        </p>
                    </form>
                </Layout>
            )
            }
        </>
    )
})

export default Registration
