import Banner from "./Banner";
import Header from "./Header";
import ProejctCard from "./project/projectComponent/ProjectCard";
import styles from "./Index.module.css";
import { useEffect, useRef, useState } from "react";
import SectionDevision from "./project/projectComponent/SectionDevision";
import { ReactComponent as Arrow } from '../images/icon/arrow.svg';
import Footer from "./Footer";
import FilterBar from "./project/projectComponent/FilterBar";
import ProCardSlider from "./project/projectComponent/ProCardSlider";
import GridSection from "./project/projectComponent/GridSection";

function ProjectList() {

    const [proCategory, setProCategory] = useState("개발");
    const [projectCategory, setProjectCategory] = useState("개발");

    return (
        <>
            <Banner/>
            <SectionDevision title="NEW PRO" sub="새로 등록한 프로를 만나보세요" finding="프로 더보기" category={proCategory} setCategory={setProCategory}/>
            <ProCardSlider category={proCategory}/>
            <SectionDevision title="NEW PROJECT" sub="새로 등록한 프로젝트들을 만나보세요" finding="프로젝트 더보기" category={projectCategory} setCategory={setProjectCategory}/>
            <GridSection category={projectCategory}>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
                <ProejctCard/>
            </GridSection>
            <div className={styles.submitContainer}>
                <button className={styles.registerPro}>
                        <span className={styles.registerText}>프로젝트 등록하기</span>
                </button>
            </div>
            <Footer/>
        </>
    )
}

export default ProjectList;