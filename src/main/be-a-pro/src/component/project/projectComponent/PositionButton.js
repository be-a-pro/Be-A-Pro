import styles from './PositionButton.module.css';

export default function PostionButton(props) {
    return (
        <div className={styles.containerOfPosition}>
            <div className={styles.infoOfPosition}>
                <div className={styles.buttonOfPosition}>
                    <span className={styles.textOfButton}>
                        {props.text}
                    </span>
                </div>
                <span>0</span>
                <span>/2</span>
            </div>
        </div>
    )
}