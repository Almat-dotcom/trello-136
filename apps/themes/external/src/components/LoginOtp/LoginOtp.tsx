import React, { memo } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "lib/i18n";
import Button from "components/parts/Button";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";
import TotpInput from "components/parts/TotpInput/TotpInput";
import {useTotpInput} from "./hooks";


type KcContext_LoginOtp = Extract<KcContext, { pageId: "login-otp.ftl" }>;

const LoginOtp = memo(
    ({ kcContext, i18n }: { kcContext: KcContext_LoginOtp; i18n: I18n } & KcProps) => {
        const { url } = kcContext;
        const { msgStr } = i18n;

        const {otp, inputsRef, handleChange, handleKeyDown} =useTotpInput();

        const handleSubmit = (e: React.FormEvent) => {
            e.preventDefault();
            const form = document.getElementById("otp-form") as HTMLFormElement;
            form.submit();
        };

        return (
            <Layout kcContext={kcContext} i18n={i18n} size="large">
                <main className="flex justify-center items-center mt-20">
                <div className="bg-white shadow-2xl rounded-lg p-6 md:p-12 lg:p-15 border border-gray-500 ">
                <div className="text-center mb-6 md:mb-10">
                            <h1 className="text-2xl md:text-4xl font-extrabold text-gray-800 mb-4 md:mb-6">
                                {msgStr("enterOtp")}
                            </h1>
                            <p className="text-md md:text-lg text-gray-600">{msgStr("setupTotp")}</p>
                        </div>

                        <form id="otp-form" method="post" action={url.loginAction} onSubmit={handleSubmit} className="space-y-6 md:space-y-8">
                            <input type="hidden" name="otp" value={otp.join("")} />
                             <TotpInput
                                otp={otp}
                                inputsRef={inputsRef}
                                handleChange={handleChange}
                                handleKeyDown={handleKeyDown}
                                containerClassName="items-center"
                                inputClassName="md:w-13 lg:w-12"/>
                            <div className="text-center mt-4 md:mt-8">
                                <Button severity="primary" type="submit">
                                    {msgStr("confirm")}
                                </Button>
                            </div>
                        </form>
                    </div>
                </main>
            </Layout>
        );
    }
);

export default LoginOtp;