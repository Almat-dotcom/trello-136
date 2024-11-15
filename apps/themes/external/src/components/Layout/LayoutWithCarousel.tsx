import Carousel from "components/parts/Carousel";
import Footer from "components/parts/Footer";
import Header from "components/parts/Header";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { ReactNode } from "react";

type LayoutProps = {
    children?: ReactNode
};

const LayoutWithCarousel = ({ children, kcContext, i18n, ...props }: { children: any, kcContext: KcContext, i18n: I18n } & LayoutProps) => {
    const { locale } = kcContext
    const { currentLanguageTag, supported } = locale ?? { currentLanguageTag: "en", supported: [] }

    return (
        <div className="bg-white">
            <Header kcContext={kcContext} current={currentLanguageTag} langs={supported} i18n={i18n} />
            <div className="flex min-h-screen">
                <div className="flex flex-col w-full max-w-lg px-6 mx-auto lg:w-1/2">
                    <div className="pt-28">
                    </div>
                    {children}
                    <div className="w-20">

                    </div>
                </div>
                <div className="hidden bg-gradient-to-tr lg:block lg:w-1/2">
                    <div className="flex flex-col items-center justify-between p-4">
                        <div className="w-full h-16">
                        </div>

                        <div className="flex items-center justify-center w-full">
                            <Carousel i18n={i18n} />
                        </div>

                        <div></div>
                    </div>
                </div>
            </div>
            <Footer i18n={i18n} kcContext={kcContext}/>
        </div>
    );
}

export default LayoutWithCarousel;
