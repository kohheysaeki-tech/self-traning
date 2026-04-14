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
        val PRESET_EXERCISES = listOf(
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
                name = "懸垂（チンニング）",
                targetMuscle = "広背筋・上腕二頭筋",
                category = "上半身",
                description = "バーを肩幅より少し広めに握り、腕を伸ばしてぶら下がります。息を吐きながら肘を曲げ、あごがバーの上に来るまで体を引き上げます。息を吸いながらゆっくりと元の姿勢に戻ります。",
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
