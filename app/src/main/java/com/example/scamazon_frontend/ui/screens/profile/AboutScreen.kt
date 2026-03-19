package com.example.scamazon_frontend.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

data class StudentInfo(val name: String, val mssv: String)

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit = {}
) {
    val students = listOf(
        StudentInfo("Lê Thành Công", "SE183504"),
        StudentInfo("Dương Thành Phát", "SE183374"),
        StudentInfo("Nguyễn Văn Hải Quân", "QE180068"),
        StudentInfo("Lê Quốc Khánh", "SE171151"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        LafyuuTopAppBar(
            title = "About",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // App logo / icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    fontSize = 42.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Scamazon",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = TextPrimary
            )

            Text(
                text = "Ứng dụng thương mại điện tử",
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // About app section
            SectionCard {
                Text(
                    text = "Giới thiệu sản phẩm",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = PrimaryBlue,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "Scamazon là ứng dụng mua sắm trực tuyến được xây dựng trong khuôn khổ môn học PRM392 tại FPT University. " +
                            "Ứng dụng cung cấp đầy đủ các tính năng mua sắm hiện đại: duyệt sản phẩm theo danh mục, " +
                            "tìm kiếm, quản lý giỏ hàng, thanh toán trực tuyến qua VNPay, theo dõi đơn hàng, " +
                            "chat hỗ trợ trực tiếp và nhiều tính năng khác.",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Students section
            SectionCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thành viên nhóm",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                }

                students.forEachIndexed { index, student ->
                    StudentRow(index = index + 1, student = student)
                    if (index < students.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = BorderLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructor section
            SectionCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Giáo viên hướng dẫn",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Thầy Ngô Đăng Hà An",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AnNDH2@fe.edu.vn",
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun StudentRow(index: Int, student: StudentInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PrimaryBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = PrimaryBlue
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = student.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = "MSSV: ${student.mssv}",
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
