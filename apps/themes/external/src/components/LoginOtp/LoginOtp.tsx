import React, { memo, useState, useRef } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "lib/i18n";
import Button from "components/parts/Button";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";

type KcContext_LoginOtp = Extract<KcContext, { pageId: "login-otp.ftl" }>;

const LoginOtp = memo(
    ({ kcContext, i18n }: { kcContext: KcContext_LoginOtp; i18n: I18n } & KcProps) => {
        const { url } = kcContext;
        const { msgStr } = i18n;

        const [otp, setOtp] = useState<string[]>(new Array(6).fill(""));
        const inputsRef = useRef<HTMLInputElement[]>([]);

        const handleChange = (value: string, index: number) => {
            const newValue = value.slice(-1);
            const newOtp = [...otp];
            newOtp[index] = newValue;
            setOtp(newOtp);

            if (newValue && index < 5) {
                inputsRef.current[index + 1]?.focus();
            }
        };

        const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
            if (e.key === "Backspace" && !otp[index] && index > 0) {
                inputsRef.current[index - 1]?.focus();
            }
        };

        const handleSubmit = (e: React.FormEvent) => {
            e.preventDefault();
            const form = document.getElementById("otp-form") as HTMLFormElement;
            form.submit();
        };

        return (
            <Layout kcContext={kcContext} i18n={i18n}>
                <main className="flex justify-center items-center min-h-screen">
                    <div className="bg-white shadow-2xl rounded-lg p-6 md:p-12 lg:p-20 border border-gray-500 max-w-md md:max-w-lg lg:max-w-xl">
                        <div className="text-center mb-6 md:mb-10">
                            <h1 className="text-2xl md:text-4xl font-extrabold text-gray-800 mb-4 md:mb-6">
                                {msgStr("enterOtp")}
                            </h1>
                            <p className="text-md md:text-lg text-gray-600">{msgStr("setupTotp")}</p>
                        </div>

                        <form id="otp-form" method="post" action={url.loginAction} onSubmit={handleSubmit} className="space-y-6 md:space-y-8">
                            <input type="hidden" name="otp" value={otp.join("")} />

                             <div className="flex justify-center gap-2 md:gap-4 max-w-fit">
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={(el) => (inputsRef.current[index] = el!)}
                                        type="text"
                                        maxLength={1}
                                        value={digit}
                                        onChange={(e) => handleChange(e.target.value, index)}
                                        onKeyDown={(e) => handleKeyDown(e, index)}
                                        className="w-10 md:w-12 lg:w-16 aspect-square text-center text-xl md:text-2xl lg:text-3xl font-bold border border-gray-300 rounded-lg focus:outline-none focus:ring-4 focus:ring-blue-500 transition-all shadow-sm"
                                    />
                                ))}
                            </div>


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