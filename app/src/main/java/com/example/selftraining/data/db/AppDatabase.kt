package com.example.selftraining.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet

@Database(
    entities = [Exercise::class, WorkoutPlan::class, WorkoutSet::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        /**
         * プリセット種目リスト。
         * 自重種目（平日向け）とジムマシン種目（土日向け）を含む。
         * 懸垂は自重が重い場合に難しいため除外し、代わりにラットプルダウン（ジム）を追加。
         */
        val PRESET_EXERCISES = listOf(
            // ── 平日向け：自重・器具不要 ──────────────────────
            Exercise(
                name = "スクワット",
                targetMuscle = "大腿四頭筋・臀部・ハムストリングス",
                category = "下半身",
                description = "足を肩幅に開いて立ち、背筋を伸ばします。息を吸いながらゆっくりと膝を曲げ、太ももが床と平行になるまで腰を下ろします。膝はつま先の方向に向けて、かかとは床につけたままにします。息を吐きながら元の姿勢に戻ります。",
                isPreset = true
            ),
            Exercise(
                name = "ランジ",
                targetMuscle = "大腿四頭筋・臀部",
                category = "下半身",
                description = "足を腰幅に開いて立ちます。片足を前に大きく踏み出し、両膝が90度になるように腰を落とします。前膝がつま先より前に出ないよう注意します。元の姿勢に戻り、反対の足で繰り返します。",
                isPreset = true
            ),
            Exercise(
                name = "腕立て伏せ",
                targetMuscle = "大胸筋・三角筋・上腕三頭筋",
                category = "上半身",
                description = "両手を肩幅より少し広めに開き、床につきます。体をまっすぐに保ちながら、息を吸いながら肘を曲げて胸が床に近づくまで体を下げます。息を吐きながら腕を伸ばして元の姿勢に戻ります。",
                isPreset = true
            ),
            Exercise(
                name = "ダンベルカール",
                targetMuscle = "上腕二頭筋",
                category = "上半身",
                description = "足を肩幅に開いて立ち、ダンベルを手のひらが前を向くように持ちます。肘を体の横に固定し、息を吐きながらダンベルを肩に向けて持ち上げます。息を吸いながらゆっくりと元の位置に戻します。",
                isPreset = true
            ),
            Exercise(
                name = "クランチ",
                targetMuscle = "腹直筋",
                category = "体幹",
                description = "仰向けに寝て、膝を90度に曲げます。両手を頭の後ろに添え、息を吐きながら腹筋を使って肩甲骨が床から離れるまで上体を起こします。腰は床についたままにします。息を吸いながら元の姿勢に戻ります。",
                isPreset = true
            ),
            Exercise(
                name = "プランク",
                targetMuscle = "腹横筋・脊柱起立筋",
                category = "体幹",
                description = "うつ伏せになり、肘と前腕を床につけます。つま先と前腕で体を支え、体が一直線になるようにします。お腹に力を入れ、その姿勢を30秒〜1分間キープします。",
                isPreset = true
            ),
            // ── 土日ジム向け：マシン・バーベル種目 ──────────────
            Exercise(
                name = "ベンチプレス",
                targetMuscle = "大胸筋・三角筋前部・上腕三頭筋",
                category = "上半身",
                description = "ベンチに仰向けになり、バーを肩幅より少し広めに握ります。バーをラックから外し、胸の上に保持します。息を吸いながらゆっくりとバーを胸まで下ろし、胸に軽く触れたら息を吐きながら力強く押し上げます。肘を完全に伸ばしきらない位置で止め、同じ動作を繰り返します。",
                isPreset = true
            ),
            Exercise(
                name = "ラットプルダウン",
                targetMuscle = "広背筋・上腕二頭筋・大円筋",
                category = "上半身",
                description = "マシンのシートに座り、太ももをパッドに固定します。バーを肩幅より広めに握り、腕を頭上に伸ばします。息を吐きながら肘を引き下げるイメージで、バーを胸の上部まで引き降ろします。広背筋の収縮を意識しながら、息を吸いながらゆっくりと元の位置に戻します。",
                isPreset = true
            ),
            Exercise(
                name = "ショルダープレス",
                targetMuscle = "三角筋・上腕三頭筋",
                category = "上半身",
                description = "シートに座り、背もたれに背中をつけます。ダンベルまたはバーベルを肩の高さで持ちます。息を吐きながら頭上に向けて垂直に押し上げます。肘が伸びきる手前で止め、息を吸いながらゆっくりと元の位置まで下ろします。",
                isPreset = true
            ),
            Exercise(
                name = "レッグプレス",
                targetMuscle = "大腿四頭筋・臀部・ハムストリングス",
                category = "下半身",
                description = "レッグプレスマシンのシートに座り、背中と腰をしっかりパッドにつけます。足を肩幅に開いてプレートに置き、膝が90度になるよう調整します。息を吐きながら脚を伸ばしてプレートを押し出します。膝を完全に伸ばしきらずに止め、息を吸いながらゆっくりと元の位置に戻します。",
                isPreset = true
            ),
            Exercise(
                name = "デッドリフト",
                targetMuscle = "ハムストリングス・臀部・脊柱起立筋",
                category = "下半身",
                description = "足を肩幅に開き、バーベルの前に立ちます。腰を落として背筋を伸ばした状態でバーを握ります。息を吸い、腹圧をかけながら脚と腰の力で立ち上がります。バーは体の近くを通るように引き上げ、息を吐きながらゆっくりと元に戻します。",
                isPreset = true
            )
        )
    }
}
