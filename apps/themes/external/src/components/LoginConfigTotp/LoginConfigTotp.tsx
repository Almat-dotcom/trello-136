import React, { memo } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";
import Button from "components/parts/Button";
import TotpInput from "components/parts/TotpInput/TotpInput";
import { useTotpInput } from "components/LoginOtp/hooks";

type KcContext_LoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const LoginConfigTotp = memo(
    ({ kcContext, i18n }: { kcContext: KcContext_LoginConfigTotp; i18n: I18n } & KcProps) => {
        const { url, totp } = kcContext;
        const { msgStr } = i18n;

        const { otp, inputsRef, handleChange, handleKeyDown } = useTotpInput();

        const handleSubmit = () => {
            const form = document.getElementById("totp-setup-form") as HTMLFormElement;
            form.submit();
        };

        return (
            <Layout kcContext={kcContext} i18n={i18n}>
                <div className="max-w-lg mx-auto bg-white shadow-lg rounded-lg p-6 border border-gray-200 mt-10">
                    <div className="text-center">
                        <h1 className="text-2xl font-bold text-gray-800 mb-4">{msgStr("verifyTitle")}</h1>
                        <p className="text-sm text-gray-600 mb-6">{msgStr("verifyDescription")}</p>
                    </div>

                    <form id="totp-setup-form" action={url.loginAction} method="post">
                        <input type="hidden" name="totpSecret" value={totp.totpSecret}/>
                        <input type="hidden" name="totp" value={otp.join("")}/>
                        <div className="text-center">
                            <p className="text-gray-700">{msgStr("downloadInstructions")}</p>
                            <img
                                src={`data:image/png;base64,${totp.totpSecretQrCode}`}
                                alt="TOTP QR Code"
                                className="mx-auto mt-4 w-40 h-40 border border-gray-300 rounded-md"
                            />
                            <p className="text-gray-700 mt-4">{msgStr("scanCodeInstructions")}</p>
                            <div className="mt-2 bg-gray-100 text-gray-900 p-3 rounded-md font-mono text-sm">
                                {totp.totpSecretEncoded}
                            </div>
                        </div>
                        <TotpInput
                            otp={otp}
                            inputsRef={inputsRef}
                            handleChange={handleChange}
                            handleKeyDown={handleKeyDown}
                            containerClassName="justify-center" // Центрируем инпуты для OTP
                            inputClassName=" mt-3 w-10 md:w-12 lg:w-9" // Размеры инпутов
                        />

                        <Button severity="primary" type="submit" onClick={handleSubmit}>
                            {msgStr("verifyButton")}
                        </Button>
                    </form>
                </div>
            </Layout>
        );
    }
);

export default LoginConfigTotp;