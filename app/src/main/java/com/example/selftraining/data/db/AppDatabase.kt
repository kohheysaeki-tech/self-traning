package com.example.selftraining.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Roomデータベース定義
 * エンティティ: Exercise, WorkoutPlan, WorkoutSet
 */
@Database(
    entities = [Exercise::class, WorkoutPlan::class, WorkoutSet::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "self_training_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 初回作成時にプリセットデータを非同期で投入する
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(
                                        database.exerciseDao(),
                                        database.workoutDao()
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * プリセット種目IDの定数
 * AppDatabase内の `populateDatabase` とワークアウトセット初期データで共用する
 */
object PresetExerciseId {
    const val WALKING_LUNGE: Long = 1L      // ウォーキングランジ
    const val HIP_LIFT: Long = 2L           // ヒップリフト（グルートブリッジ）
    const val CALF_RAISE: Long = 3L         // カーフレイズ
    const val LEG_RAISE: Long = 4L          // レッグレイズ
    const val PUSH_UP: Long = 5L            // 腕立て伏せ
    const val DUMBBELL_CURL: Long = 6L      // ダンベルカール
    const val CRUNCH: Long = 7L             // クランチ
    const val PLANK: Long = 8L              // プランク
    const val MOUNTAIN_CLIMBER: Long = 9L   // マウンテンクライマー
    const val BIRD_DOG: Long = 10L          // バードドッグ
}

/**
 * プリセットプランIDの定数（曜日順: 月=1〜日=7）
 */
object PresetPlanId {
    const val MONDAY: Long = 1L
    const val TUESDAY: Long = 2L
    const val WEDNESDAY: Long = 3L
    const val THURSDAY: Long = 4L   // 休息日
    const val FRIDAY: Long = 5L
    const val SATURDAY: Long = 6L
    const val SUNDAY: Long = 7L     // 休息日
}

/**
 * 初期データ投入関数
 * ユーザーの目標（脚やせ・ダイエット）に合わせた種目と週間プログラムを登録する
 * ※ 懸垂は体重が重くてできないため除外
 */
suspend fun populateDatabase(exerciseDao: ExerciseDao, workoutDao: WorkoutDao) {
    // ── プリセット種目データ ───────────────────────────────────
    // IDを明示指定することで、ワークアウトセット初期データとの紐付けを安全にする
    val exercises = listOf(
        Exercise(
            id = PresetExerciseId.WALKING_LUNGE,
            name = "ウォーキングランジ",
            targetMuscle = "大腿四頭筋・臀部・ハムストリングス",
            category = "下半身",
            description = "脚の引き締め・有酸素効果のある下半身エクササイズです。高負荷にならないよう自重で行い、筋持久力を高めることで脚を細く引き締めます。",
            steps = "1. 足を腰幅に開いて真っすぐ立ちます。\n2. 右足を大きく一歩前に踏み出します。\n3. 前膝が90度になるように、後ろ膝をゆっくり床に近づけます（床に触れないよう注意）。\n4. 前足で地面を蹴り、後ろ足を前に引き寄せて立ちます（歩くように前進）。\n5. 左右交互に繰り返します。\nポイント: 膝がつま先より前に出ないよう注意。上半身は真っすぐ保ちましょう。"
        ),
        Exercise(
            id = PresetExerciseId.HIP_LIFT,
            name = "ヒップリフト（グルートブリッジ）",
            targetMuscle = "臀部・ハムストリングス",
            category = "下半身",
            description = "お尻・太もも裏を鍛えることで脚やせ・ヒップアップ効果が期待できるエクササイズです。体重が重くても安全に行えます。",
            steps = "1. 仰向けに寝て、膝を立てます（足は腰幅）。\n2. 腕は体の横に置き、手のひらを床につけます。\n3. お腹に力を入れ、お尻をゆっくり持ち上げます。\n4. 腰・お尻・膝が一直線になるまで上げ、2秒キープします。\n5. ゆっくりお尻を下ろし、床ぎりぎりで止めて再び上げます。\nポイント: 腰を反りすぎないよう、お腹を締めながら行いましょう。"
        ),
        Exercise(
            id = PresetExerciseId.CALF_RAISE,
            name = "カーフレイズ",
            targetMuscle = "ふくらはぎ（腓腹筋・ヒラメ筋）",
            category = "下半身",
            description = "ふくらはぎを引き締めるエクササイズです。立ったままできるので、隙間時間にも取り組めます。",
            steps = "1. 足を腰幅に開いて真っすぐ立ちます（壁や椅子に軽く手を置いてもOK）。\n2. かかとをゆっくり持ち上げ、つま先立ちになります。\n3. 最上部で1秒キープします。\n4. ゆっくりかかとを下ろし、床に付く直前で止めます。\n5. これを繰り返します。\nポイント: ゆっくり動かすことで筋肉に効かせましょう。反動を使わないのがコツです。"
        ),
        Exercise(
            id = PresetExerciseId.LEG_RAISE,
            name = "レッグレイズ",
            targetMuscle = "腸腰筋・腹直筋下部",
            category = "体幹",
            description = "下腹部・脚付け根（腸腰筋）を鍛えることで、脚の引き締め効果と下腹のたるみ改善が期待できます。",
            steps = "1. 仰向けに寝て、脚をまっすぐ伸ばします。\n2. 手は体の横か、お尻の下に置きます。\n3. お腹に力を入れ、脚を揃えたまま床から約30〜45度まで持ち上げます。\n4. ゆっくり脚を下ろし、床ぎりぎりで止めます。\n5. 再び持ち上げる動作を繰り返します。\nポイント: 腰が床から浮かないよう、常にお腹を締めて行いましょう。"
        ),
        Exercise(
            id = PresetExerciseId.PUSH_UP,
            name = "腕立て伏せ",
            targetMuscle = "大胸筋・三角筋・上腕三頭筋",
            category = "上半身",
            description = "胸・肩・腕の上半身を鍛えます。体重が重い場合は膝をついた「膝つき腕立て伏せ」から始めましょう。",
            steps = "1. 腕を肩幅より少し広めにして床につきます（膝つきの場合は膝を床に）。\n2. 体をまっすぐに保ちます（お尻を上げたり下げたりしない）。\n3. 肘を曲げながら、胸が床に近づくまでゆっくり下ろします。\n4. 腕を伸ばしてゆっくり体を持ち上げます。\n5. これを繰り返します。\nポイント: 呼吸を止めないこと。下ろすとき息を吸い、上げるとき吐きましょう。"
        ),
        Exercise(
            id = PresetExerciseId.DUMBBELL_CURL,
            name = "ダンベルカール",
            targetMuscle = "上腕二頭筋",
            category = "上半身",
            description = "腕の力こぶ（上腕二頭筋）を鍛えます。軽い重量（500mlペットボトルでもOK）から始めましょう。",
            steps = "1. 足を肩幅に開いて立ち（または座って）、両手にダンベルを持ちます。\n2. 肘を体の脇に固定し、手のひらを上に向けます。\n3. 肘を動かさずに前腕をゆっくり持ち上げ、肩に近づけます。\n4. 最上部で1秒キープし、ゆっくり下ろします。\n5. 左右交互または両手同時に行います。\nポイント: 肘が前後に動かないよう固定しましょう。反動を使わないのがコツです。"
        ),
        Exercise(
            id = PresetExerciseId.CRUNCH,
            name = "クランチ",
            targetMuscle = "腹直筋",
            category = "体幹",
            description = "腹筋（腹直筋）を集中的に鍛えます。シットアップより腰への負担が少なく、初心者にもおすすめです。",
            steps = "1. 仰向けに寝て、膝を立てます。\n2. 手を頭の後ろか胸の前でクロスします（首を引っ張らない）。\n3. お腹を丸めるようにして、肩甲骨が床から離れる程度まで上体を起こします。\n4. 腹筋に力が入っているのを感じながら、ゆっくり上体を下ろします。\n5. これを繰り返します。\nポイント: 首に力を入れず、お腹の力で持ち上げましょう。"
        ),
        Exercise(
            id = PresetExerciseId.PLANK,
            name = "プランク",
            targetMuscle = "腹横筋・脊柱起立筋",
            category = "体幹",
            description = "体幹全体を強化し、姿勢改善にも効果的なエクササイズです。体重が重くても無理なく行えます。",
            steps = "1. うつ伏せになり、肘を床につけます（肘は肩の真下）。\n2. つま先を立て、体を一直線に保ちます。\n3. お腹・お尻に力を入れ、その姿勢をキープします。\n4. まず20〜30秒を目標に保持し、慣れたら時間を延ばします。\nポイント: お尻を上げたり下げたりしないよう一直線を意識。呼吸を止めないこと。"
        ),
        Exercise(
            id = PresetExerciseId.MOUNTAIN_CLIMBER,
            name = "マウンテンクライマー",
            targetMuscle = "腸腰筋・腹筋・全身",
            category = "体幹",
            description = "有酸素効果が高く脂肪燃焼に効果的な全身エクササイズです。心拍数を上げてダイエット効果を高めます。",
            steps = "1. 腕立て伏せの姿勢（腕をまっすぐ伸ばした状態）になります。\n2. 体を一直線に保ちながら、右膝を胸に向けて引き寄せます。\n3. 右足を戻しながら、すぐに左膝を胸に向けて引き寄せます。\n4. 走るように左右交互に素早く繰り返します。\n5. 20〜30秒を目標に行いましょう。\nポイント: お尻が上がらないよう体のラインを保ちましょう。息を切らすくらいのペースが効果的です。"
        ),
        Exercise(
            id = PresetExerciseId.BIRD_DOG,
            name = "バードドッグ",
            targetMuscle = "脊柱起立筋・臀部・腹横筋",
            category = "体幹",
            description = "体幹の安定性を高め、腰痛予防にも効果的なエクササイズです。体重が重くても安全に実施できます。",
            steps = "1. 四つん這いになり、手は肩の真下、膝は腰の真下に置きます。\n2. お腹に力を入れ、背中を平らに保ちます。\n3. 右腕を前に、左足を後ろに同時にゆっくり伸ばします。\n4. 腕と脚が床と平行になったら2秒キープします。\n5. ゆっくり元の位置に戻し、反対側（左腕・右足）も同様に行います。\nポイント: 腰をねじらず、体を水平に保ちましょう。バランスが難しければ足だけから始めてもOKです。"
        )
    )
    exerciseDao.insertExercises(exercises)

    // ── おすすめ週間プラン ────────────────────────────────────
    // IDを明示指定してワークアウトセットとの紐付けを安全にする
    val plans = listOf(
        WorkoutPlan(id = PresetPlanId.MONDAY,    dayOfWeek = 0, planName = "下半身引き締め", isRestDay = false),
        WorkoutPlan(id = PresetPlanId.TUESDAY,   dayOfWeek = 1, planName = "上半身トレーニング", isRestDay = false),
        WorkoutPlan(id = PresetPlanId.WEDNESDAY, dayOfWeek = 2, planName = "体幹トレーニング", isRestDay = false),
        WorkoutPlan(id = PresetPlanId.THURSDAY,  dayOfWeek = 3, planName = "休息日", isRestDay = true),
        WorkoutPlan(id = PresetPlanId.FRIDAY,    dayOfWeek = 4, planName = "全身＋脂肪燃焼", isRestDay = false),
        WorkoutPlan(id = PresetPlanId.SATURDAY,  dayOfWeek = 5, planName = "下半身＋体幹", isRestDay = false),
        WorkoutPlan(id = PresetPlanId.SUNDAY,    dayOfWeek = 6, planName = "休息日", isRestDay = true)
    )
    workoutDao.insertPlans(plans)

    // ── 初期ワークアウトセット ────────────────────────────────
    val workoutSets = listOf(
        // 月曜: 下半身引き締め
        WorkoutSet(planId = PresetPlanId.MONDAY, exerciseId = PresetExerciseId.WALKING_LUNGE, sets = 3, reps = 12, sortOrder = 0),
        WorkoutSet(planId = PresetPlanId.MONDAY, exerciseId = PresetExerciseId.HIP_LIFT,       sets = 3, reps = 15, sortOrder = 1),
        WorkoutSet(planId = PresetPlanId.MONDAY, exerciseId = PresetExerciseId.CALF_RAISE,     sets = 3, reps = 20, sortOrder = 2),
        // 火曜: 上半身
        WorkoutSet(planId = PresetPlanId.TUESDAY, exerciseId = PresetExerciseId.PUSH_UP,       sets = 3, reps = 10, sortOrder = 0),
        WorkoutSet(planId = PresetPlanId.TUESDAY, exerciseId = PresetExerciseId.DUMBBELL_CURL, sets = 3, reps = 12, weightKg = 2f, sortOrder = 1),
        // 水曜: 体幹
        WorkoutSet(planId = PresetPlanId.WEDNESDAY, exerciseId = PresetExerciseId.PLANK,     sets = 3, reps = 30, sortOrder = 0),
        WorkoutSet(planId = PresetPlanId.WEDNESDAY, exerciseId = PresetExerciseId.CRUNCH,    sets = 3, reps = 15, sortOrder = 1),
        WorkoutSet(planId = PresetPlanId.WEDNESDAY, exerciseId = PresetExerciseId.LEG_RAISE, sets = 3, reps = 12, sortOrder = 2),
        // 金曜: 全身＋脂肪燃焼
        WorkoutSet(planId = PresetPlanId.FRIDAY, exerciseId = PresetExerciseId.MOUNTAIN_CLIMBER, sets = 3, reps = 20, sortOrder = 0),
        WorkoutSet(planId = PresetPlanId.FRIDAY, exerciseId = PresetExerciseId.BIRD_DOG,         sets = 3, reps = 10, sortOrder = 1),
        WorkoutSet(planId = PresetPlanId.FRIDAY, exerciseId = PresetExerciseId.HIP_LIFT,         sets = 3, reps = 15, sortOrder = 2),
        // 土曜: 下半身＋体幹
        WorkoutSet(planId = PresetPlanId.SATURDAY, exerciseId = PresetExerciseId.WALKING_LUNGE, sets = 3, reps = 12, sortOrder = 0),
        WorkoutSet(planId = PresetPlanId.SATURDAY, exerciseId = PresetExerciseId.CALF_RAISE,    sets = 3, reps = 20, sortOrder = 1),
        WorkoutSet(planId = PresetPlanId.SATURDAY, exerciseId = PresetExerciseId.CRUNCH,        sets = 3, reps = 15, sortOrder = 2)
    )
    workoutDao.insertWorkoutSets(workoutSets)
}
