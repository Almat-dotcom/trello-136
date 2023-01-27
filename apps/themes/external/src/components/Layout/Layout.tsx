import Footer from "components/parts/Footer";
import Header from "components/parts/Header";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";

const Layout = ({ children, kcContext, i18n, ...props }: { children: any, kcContext: KcContext, i18n: I18n }) => {
    const { locale } = kcContext;
    const { currentLanguageTag, supported } = locale ?? { currentLanguageTag: "en", supported: [] }
    return (
        <div>
            <div className="bg-gradient-to-tr">
                <Header current={currentLanguageTag} langs={supported} i18n={i18n}/> 
                <div className="flex justify-between items-stretch pt-20 min-h-screen p-2">
                    <div className="hidden lg:block">
                    </div>

                    <div className="flex flex-col items-center lg:block bg-white w-full lg:w-1/2 xl:w-1/3 mt-0 sm:m-4 px-6 md:px-24 pb-6">
                        <div className="flex justify-between w-full lg:hidden mb-4">
                        </div>
                        <div className="w-full sm:w-2/3 lg:w-full">{children}</div>
                    </div>


                    <div className="hidden lg:flex lg:flex-col items-end p-2">
                    </div>
                </div>
            </div>
            <Footer i18n={i18n} />
        </div>
    );
};

export default Layout;
