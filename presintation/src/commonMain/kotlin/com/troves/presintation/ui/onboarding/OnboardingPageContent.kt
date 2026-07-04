package com.troves.presintation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardingPageContent(pageInfo: OnboardingPageInfo) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // الصورة الخلفية مغطية الجزء العلوي
        Image(
            painter = painterResource(pageInfo.imageRes),
            contentDescription = pageInfo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f) // تقليل الارتفاع قليلاً ليعطي مساحة انسيابية للكيرف والنصوص
        )

        // الـ Card البيضاء المنحنية من زاوية واحدة كبيرة
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.50f) // زيادة الارتفاع لتبدأ من منتصف الصورة وتغطي الأسفل بالكامل
                .align(Alignment.BottomCenter)
                .clip(
                    RoundedCornerShape(
                        topStart = 64.dp, // الكيرف الدائري الانسيابي من جهة اليسار العلوية مثل الصورة
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
                .background(Theme.colors.backGround)
                .padding(horizontal = Theme.spacing.extraLarge)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = pageInfo.title,
                style = Theme.typography.display.copy(
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(Theme.spacing.medium))
            Text(
                text = pageInfo.description,
                style = Theme.typography.body.large.copy(
                    lineHeight = 22.sp
                ),
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}