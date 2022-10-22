import Banner from "../Banner";
import Header from "../Header";
import FilterBar from "./projectComponent/FilterBar";
import styles from './ProjectList.module.css';
import { ReactComponent as CheckBox } from '../../images/icons/checkBox.svg';
import { ReactComponent as CheckBox_Checked } from '../../images/icons/checkBox-checked.svg';
import { useState } from 'react';
import ProejctCard from "./projectComponent/ProjectCard";
import GridSection from "./projectComponent/GridSection";
import Footer from "../Footer";

function ProjectList() {

    const [check, Setcheck] = useState(true);
    const [checkSec, SetcheckSec] = useState(true);
    const [projectCategory, setProjectCategory] = useState("개발");
    const dataArea = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18];

    function onClick() {
        Setcheck(!check);
    }

    function onClickSec() {
        SetcheckSec(!checkSec);
    }



    return (
        <>
            <Banner/>
            <div className={styles.container}>
                <div className={styles.titleContainer}>
                    <span className={styles.listTitle}> PROJECTS </span>
                </div>
                <div className={styles.classifyContainer}>
                    <div className={styles.classifyBar}>
                                <FilterBar category={projectCategory} setCategory={setProjectCategory}/>
                            <div className={styles.checkBoxLocation}>
                                <div className={styles.checkBoxBar}>
                                    <span className={styles.checkBoxText}>조회순</span>
                                    {(!check) ? <CheckBox onClick={onClick}/> : <CheckBox_Checked onClick={onClick}/>}
                                </div>
                                <div className={styles.checkBoxBar}>
                                    <span className={styles.checkBoxText2}>모집완료제외</span>
                                    {(checkSec) ? <CheckBox onClick={onClickSec}/> : <CheckBox_Checked onClick={onClickSec}/>}
                                </div>
                            </div>
                    </div>
                </div>
                <GridSection>
                    {dataArea.map(function(key) {
                        return <ProejctCard key={key}/>
                    })}
                </GridSection>
                <Footer/>
            </div>

        </>
    )
}

export default ProjectList;