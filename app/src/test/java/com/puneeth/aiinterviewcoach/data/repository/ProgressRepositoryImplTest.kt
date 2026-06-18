import com.google.common.truth.Truth.assertThat
import com.puneeth.aiinterviewcoach.domain.model.CategoryProgress
import com.puneeth.aiinterviewcoach.domain.model.DifficultyProgress
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import org.junit.Test

class ProgressRepositoryImplTest {
    @Test
    fun `category progress calculates completion percent`() {
        val progress = CategoryProgress(
            category = InterviewCategory.KOTLIN,
            completedCount = 3,
            totalCount = 12,
        )
        assertThat(progress.completionPercent).isEqualTo(25)
    }

    @Test
    fun `difficulty progress calculates completion percent`() {
        val progress = DifficultyProgress(
            difficulty = InterviewDifficulty.ADVANCED,
            completedCount = 4,
            totalCount = 10,
        )
        assertThat(progress.completionPercent).isEqualTo(40)
    }
}
