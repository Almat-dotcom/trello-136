import { Carousel as PrimeCarousel } from "primereact/carousel";
import News from "./news.jpg";
import News2 from "./news_2.jpg";
import News3 from "./news_3.jpg";
import ArrowBack from "./arrow_back.svg";
import ArrowForward from "./arrow_forward.svg";
import { useState } from "react";
import { I18n } from "lib/i18n";

const Carousel = ({ i18n } : { i18n: I18n }) => {
    const { msgStr } = i18n;
    const [page, setPage] = useState(0);

    const next = () => {
        if (page === (data.length - 1)) {
            setPage(0);
        } else {
            setPage(page + 1);
        }
    }

    const prev = () => {
        if (page === 0) {
            setPage(data.length - 1);
        } else {
            setPage(page - 1);
        }
    }

    const articleTemplate = (article: any) => {
        return (
            <div>
                <div className="flex flex-col items-center m-4">
                    <div className="m-3 w-11/12">
                        <img
                            src={article.img}
                            alt={article.name}
                            className="w-full rounded-3xl" />
                    </div>
                    <div className="pl-6">
                        <div className="w-full pb-2 text-slate-500 text-sm text-left">{article.date}</div>
                        <span>{article.text}</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="w-5/6 py-8">
            <div>
                <PrimeCarousel
                    autoplayInterval={10 * 1000}
                    value={data}
                    numVisible={1}
                    numScroll={1}
                    page={page}
                    showNavigators={false}
                    itemTemplate={articleTemplate}
                    header={""}
                    indicatorsContentClassName="w-fit"
                />
                <div className="relative w-24" style={{ top: '-46px', left: '82%' }}>
                    <button className="ml-4 px-0.5 py-1 rounded-md border-2 border-transparent transition-colors" onClick={() => prev()}>
                        <img className="w-4" style={{ marginLeft: '0.3rem' }} src={ArrowBack} alt="arrow_back" />
                    </button>
                    <button className="ml-1 px-1 py-1 rounded-md border-2 border-transparent transition-colors" onClick={() => next()}>
                        <img className="w-4" style={{ marginLeft: '0.04rem', marginRight: '0.02rem' }} src={ArrowForward} alt="arrow_back" />
                    </button>
                </div>
            </div>
            <div>
                <a className="ml-9 text-secondary-dark font-semibold underline" href="https://cabinet.kacd.kz/news">{msgStr("allNews")} &#187;</a>
            </div>
        </div>
    );
}

export default Carousel;

const data = [{
    name: "news-1",
    date: "21 Октября 2021",
    img: News,
    text: "Утверждены поправки в План закупок товаров, работ и услуг Центрального депозитария на 2022 год"
}, {
    name: "news-2",
    date: "21 Октября 2021",
    img: News2,
    text: "Результаты заочных голосований членов Совета директоров Центрального депозитария"
}, {
    name: "news-3",
    date: "21 Октября 2021",
    img: News3,
    text: "Обновлены сведения об аукционах краткосрочных нот Национального Банка РК за последние 6 месяцев"
}]
