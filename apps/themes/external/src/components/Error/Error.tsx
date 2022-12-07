import { Layout } from "components/Layout";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

type KcContext_Error = Extract<KcContext, { pageId: "error.ftl" }>;

const Error = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Error, i18n: I18n } & KcProps) => {
    const { message, client } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    <p className="mt-3 text-slate-900 text-2xl font-bold">{msgStr("errorTitle")}</p>
                </div>

                <div className="mt-4">
                    <p className="text-gray-700">{advancedMsgStr(message.summary) ?? message.summary}</p>
                </div>

                <div className="mt-4">
                    {(() => {
                        if (client?.baseUrl) {
                            return (
                                <p className="text-gray-700"><a href={client.baseUrl} className="font-bold text-blue-700 hover:text-primary hover:underline">{msg("backToApplication")}</a></p>
                            );
                        }
                    })()}
                </div>
            </div>
        </Layout>
    );
})

export default Error;
