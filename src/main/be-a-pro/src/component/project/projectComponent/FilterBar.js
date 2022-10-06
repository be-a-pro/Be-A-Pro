import styles from './FilterBar.module.css';

function FilterBar(props) {

    //console.log(props.category);

    const categories = [
        {
            name : "분류"
        },
        {
            name : "기획"
        },
        {
            name : "디자인"

        },
        {
            name : "개발"
        },
        {
            name : "기타"
        },  
    ];

    function filterOnclick(e) {
        props.setCategory(e.target.innerText);
    }
    
    return (
        <div className={styles.filterButtonBar}>
            {categories.map((item, id) => {
                return (
                    <div key={id} onClick={filterOnclick} className={props.category === item.name ? styles.filterButton_selected : styles.filterButton}>
                        <span className={props.category === item.name ? styles.filterSpan_selected : styles.filterSpan} onClick={filterOnclick}>
                            {item.name}
                        </span>
                    </div>
                )
                })}
        </div>
    )
}

export default FilterBar;