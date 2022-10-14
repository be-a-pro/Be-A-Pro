import styles from './PositionOfMenu.module.css';

export default function PositionOfMenu(props) {
    return (
        <div className={styles.positionOfMenu}>
            <span className={styles.nameOfPosition}>{props.text}</span>
            <div className={styles.numberOfApply}>
                <span>0</span>
                <span>/1</span>
            </div>
        </div>
    )
}