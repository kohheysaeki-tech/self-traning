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
 * 初期データ投入関数
 * ユーザーの目標（脚やせ・ダイエット）に合わせた種目と週間プログラムを登録する
 * ※ 懸垂は体重が重くてできないため除外
 */
suspend fun populateDatabase(exerciseDao: ExerciseDao, workoutDao: WorkoutDao) {
    // ── プリセット種目データ ───────────────────────────────────
    val exercises = listOf(
        Exercise(
            name = "ウォーキングランジ",
            targetMuscle = "大腿四頭筋・臀部・ハムストリングス",
            category = "下半身",
            description = "脚の引き締め・有酸素効果のある下半身エクササイズです。高負荷にならないよう自重で行い、筋持久力を高めることで脚を細く引き締めます。",
            steps = "1. 足を腰幅に開いて真っすぐ立ちます。\n2. 右足を大きく一歩前に踏み出します。\n3. 前膝が90度になるように、後ろ膝をゆっくり床に近づけます（床に触れないよう注意）。\n4. 前足で地面を蹴り、後ろ足を前に引き寄せて立ちます（歩くように前進）。\n5. 左右交互に繰り返します。\nポイント: 膝がつま先より前に出ないよう注意。上半身は真っすぐ保ちましょう。"
        ),
        Exercise(
            name = "ヒップリフト（グルートブリッジ）",
            targetMuscle = "臀部・ハムストリングス",
            category = "下半身",
            description = "お尻・太もも裏を鍛えることで脚やせ・ヒップアップ効果が期待できるエクササイズです。体重が重くても安全に行えます。",
            steps = "1. 仰向けに寝て、膝を立てます（足は腰幅）。\n2. 腕は体の横に置き、手のひらを床につけます。\n3. お腹に力を入れ、お尻をゆっくり持ち上げます。\n4. 腰・お尻・膝が一直線になるまで上げ、2秒キープします。\n5. ゆっくりお尻を下ろし、床ぎりぎりで止めて再び上げます。\nポイント: 腰を反りすぎないよう、お腹を締めながら行いましょう。"
        ),
        Exercise(
            name = "カーフレイズ",
            targetMuscle = "ふくらはぎ（腓腹筋・ヒラメ筋）",
            category = "下半身",
            description = "ふくらはぎを引き締めるエクササイズです。立ったままできるので、隙間時間にも取り組めます。",
            steps = "1. 足を腰幅に開いて真っすぐ立ちます（壁や椅子に軽く手を置いてもOK）。\n2. かかとをゆっくり持ち上げ、つま先立ちになります。\n3. 最上部で1秒キープします。\n4. ゆっくりかかとを下ろし、床に付く直前で止めます。\n5. これを繰り返します。\nポイント: ゆっくり動かすことで筋肉に効かせましょう。反動を使わないのがコツです。"
        ),
        Exercise(
            name = "レッグレイズ",
            targetMuscle = "腸腰筋・腹直筋下部",
            category = "体幹",
            description = "下腹部・脚付け根（腸腰筋）を鍛えることで、脚の引き締め効果と下腹のたるみ改善が期待できます。",
            steps = "1. 仰向けに寝て、脚をまっすぐ伸ばします。\n2. 手は体の横か、お尻の下に置きます。\n3. お腹に力を入れ、脚を揃えたまま床から約30〜45度まで持ち上げます。\n4. ゆっくり脚を下ろし、床ぎりぎりで止めます。\n5. 再び持ち上げる動作を繰り返します。\nポイント: 腰が床から浮かないよう、常にお腹を締めて行いましょう。"
        ),
        Exercise(
            name = "腕立て伏せ",
            targetMuscle = "大胸筋・三角筋・上腕三頭筋",
            category = "上半身",
            description = "胸・肩・腕の上半身を鍛えます。体重が重い場合は膝をついた「膝つき腕立て伏せ」から始めましょう。",
            steps = "1. 腕を肩幅より少し広めにして床につきます（膝つきの場合は膝を床に）。\n2. 体をまっすぐに保ちます（お尻を上げたり下げたりしない）。\n3. 肘を曲げながら、胸が床に近づくまでゆっくり下ろします。\n4. 腕を伸ばしてゆっくり体を持ち上げます。\n5. これを繰り返します。\nポイント: 呼吸を止めないこと。下ろすとき息を吸い、上げるとき吐きましょう。"
        ),
        Exercise(
            name = "ダンベルカール",
            targetMuscle = "上腕二頭筋",
            category = "上半身",
            description = "腕の力こぶ（上腕二頭筋）を鍛えます。軽い重量（500mlペットボトルでもOK）から始めましょう。",
            steps = "1. 足を肩幅に開いて立ち（または座って）、両手にダンベルを持ちます。\n2. 肘を体の脇に固定し、手のひらを上に向けます。\n3. 肘を動かさずに前腕をゆっくり持ち上げ、肩に近づけます。\n4. 最上部で1秒キープし、ゆっくり下ろします。\n5. 左右交互または両手同時に行います。\nポイント: 肘が前後に動かないよう固定しましょう。反動を使わないのがコツです。"
        ),
        Exercise(
            name = "クランチ",
            targetMuscle = "腹直筋",
            category = "体幹",
            description = "腹筋（腹直筋）を集中的に鍛えます。シットアップより腰への負担が少なく、初心者にもおすすめです。",
            steps = "1. 仰向けに寝て、膝を立てます。\n2. 手を頭の後ろか胸の前でクロスします（首を引っ張らない）。\n3. お腹を丸めるようにして、肩甲骨が床から離れる程度まで上体を起こします。\n4. 腹筋に力が入っているのを感じながら、ゆっくり上体を下ろします。\n5. これを繰り返します。\nポイント: 首に力を入れず、お腹の力で持ち上げましょう。"
        ),
        Exercise(
            name = "プランク",
            targetMuscle = "腹横筋・脊柱起立筋",
            category = "体幹",
            description = "体幹全体を強化し、姿勢改善にも効果的なエクササイズです。体重が重くても無理なく行えます。",
            steps = "1. うつ伏せになり、肘を床につけます（肘は肩の真下）。\n2. つま先を立て、体を一直線に保ちます。\n3. お腹・お尻に力を入れ、その姿勢をキープします。\n4. まず20〜30秒を目標に保持し、慣れたら時間を延ばします。\nポイント: お尻を上げたり下げたりしないよう一直線を意識。呼吸を止めないこと。"
        ),
        Exercise(
            name = "マウンテンクライマー",
            targetMuscle = "腸腰筋・腹筋・全身",
            category = "体幹",
            description = "有酸素効果が高く脂肪燃焼に効果的な全身エクササイズです。心拍数を上げてダイエット効果を高めます。",
            steps = "1. 腕立て伏せの姿勢（腕をまっすぐ伸ばした状態）になります。\n2. 体を一直線に保ちながら、右膝を胸に向けて引き寄せます。\n3. 右足を戻しながら、すぐに左膝を胸に向けて引き寄せます。\n4. 走るように左右交互に素早く繰り返します。\n5. 20〜30秒を目標に行いましょう。\nポイント: お尻が上がらないよう体のラインを保ちましょう。息を切らすくらいのペースが効果的です。"
        ),
        Exercise(
            name = "バードドッグ",
            targetMuscle = "脊柱起立筋・臀部・腹横筋",
            category = "体幹",
            description = "体幹の安定性を高め、腰痛予防にも効果的なエクササイズです。体重が重くても安全に実施できます。",
            steps = "1. 四つん這いになり、手は肩の真下、膝は腰の真下に置きます。\n2. お腹に力を入れ、背中を平らに保ちます。\n3. 右腕を前に、左足を後ろに同時にゆっくり伸ばします。\n4. 腕と脚が床と平行になったら2秒キープします。\n5. ゆっくり元の位置に戻し、反対側（左腕・右足）も同様に行います。\nポイント: 腰をねじらず、体を水平に保ちましょう。バランスが難しければ足だけから始めてもOKです。"
        )
    )
    exerciseDao.insertExercises(exercises)

    // ── おすすめ週間プラン ────────────────────────────────────
    val plans = listOf(
        WorkoutPlan(dayOfWeek = 0, planName = "下半身引き締め", isRestDay = false),     // 月
        WorkoutPlan(dayOfWeek = 1, planName = "上半身トレーニング", isRestDay = false),  // 火
        WorkoutPlan(dayOfWeek = 2, planName = "体幹トレーニング", isRestDay = false),    // 水
        WorkoutPlan(dayOfWeek = 3, planName = "休息日", isRestDay = true),              // 木
        WorkoutPlan(dayOfWeek = 4, planName = "全身＋脂肪燃焼", isRestDay = false),     // 金
        WorkoutPlan(dayOfWeek = 5, planName = "下半身＋体幹", isRestDay = false),       // 土
        WorkoutPlan(dayOfWeek = 6, planName = "休息日", isRestDay = true)               // 日
    )
    workoutDao.insertPlans(plans)

    // ── 初期ワークアウトセット ────────────────────────────────
    // 種目IDは exercises の挿入順（1〜10）に対応:
    //   1=ウォーキングランジ, 2=ヒップリフト, 3=カーフレイズ, 4=レッグレイズ,
    //   5=腕立て伏せ, 6=ダンベルカール, 7=クランチ, 8=プランク,
    //   9=マウンテンクライマー, 10=バードドッグ
    // プランIDは plans の挿入順（1〜7）に対応:
    //   1=月曜, 2=火曜, 3=水曜, 4=木曜(休), 5=金曜, 6=土曜, 7=日曜(休)
    val workoutSets = listOf(
        // 月曜: 下半身引き締め（planId=1）
        WorkoutSet(planId = 1, exerciseId = 1, sets = 3, reps = 12, sortOrder = 0),  // ウォーキングランジ
        WorkoutSet(planId = 1, exerciseId = 2, sets = 3, reps = 15, sortOrder = 1),  // ヒップリフト
        WorkoutSet(planId = 1, exerciseId = 3, sets = 3, reps = 20, sortOrder = 2),  // カーフレイズ
        // 火曜: 上半身（planId=2）
        WorkoutSet(planId = 2, exerciseId = 5, sets = 3, reps = 10, sortOrder = 0),  // 腕立て伏せ
        WorkoutSet(planId = 2, exerciseId = 6, sets = 3, reps = 12, weightKg = 2f, sortOrder = 1),  // ダンベルカール
        // 水曜: 体幹（planId=3）
        WorkoutSet(planId = 3, exerciseId = 8, sets = 3, reps = 30, sortOrder = 0),  // プランク（reps=秒数）
        WorkoutSet(planId = 3, exerciseId = 7, sets = 3, reps = 15, sortOrder = 1),  // クランチ
        WorkoutSet(planId = 3, exerciseId = 4, sets = 3, reps = 12, sortOrder = 2),  // レッグレイズ
        // 金曜: 全身＋脂肪燃焼（planId=5）
        WorkoutSet(planId = 5, exerciseId = 9, sets = 3, reps = 20, sortOrder = 0),  // マウンテンクライマー
        WorkoutSet(planId = 5, exerciseId = 10, sets = 3, reps = 10, sortOrder = 1), // バードドッグ
        WorkoutSet(planId = 5, exerciseId = 2, sets = 3, reps = 15, sortOrder = 2),  // ヒップリフト
        // 土曜: 下半身＋体幹（planId=6）
        WorkoutSet(planId = 6, exerciseId = 1, sets = 3, reps = 12, sortOrder = 0),  // ウォーキングランジ
        WorkoutSet(planId = 6, exerciseId = 3, sets = 3, reps = 20, sortOrder = 1),  // カーフレイズ
        WorkoutSet(planId = 6, exerciseId = 7, sets = 3, reps = 15, sortOrder = 2)   // クランチ
    )
    workoutDao.insertWorkoutSets(workoutSets)
}
