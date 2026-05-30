package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmCashCollectionScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    orderId: String = "#DD-8821",
    amountToCollect: Double = 850.0
) {
    val lang = viewModel.currentLanguage

    // Interactive states
    var isCashReceivedCheck by remember { mutableStateOf(false) }
    var signatureLines by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }
    var currentLine by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val hasSigned = signatureLines.isNotEmpty() || currentLine.isNotEmpty()

    // Handle asynchronous completion processing simulation
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(1500)
            isProcessing = false
            isCompleted = true
            
            // Sync status to viewmodel
            viewModel.isPartnerOrderDelivered = true
            viewModel.partnerDeliveriesCount += 1
            viewModel.partnerDistanceKms += 2.4
            viewModel.partnerMilestoneStep = 3 // Delivered state
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!isCompleted) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Confirm Cash Collection" else "नकद संग्रह की पुष्टि",
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyBlue,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("cash_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NavyBlue
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .background(LightGreen, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(IndianGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Online" else "सक्रिय",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndianGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            if (!isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    val isButtonEnabled = isCashReceivedCheck && hasSigned && !isProcessing
                    Button(
                        onClick = { isProcessing = true },
                        enabled = isButtonEnabled,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Saffron,
                            disabledContainerColor = Color(0xFFFFDCC2)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("complete_delivery_button")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Processing..." else "प्रोसेसिंग...",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Confirm & Complete Delivery" else "पुष्टि करें और आर्डर पूरा करें",
                                color = if (isButtonEnabled) Color.White else TextVariant.copy(alpha = 0.5f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        if (isCompleted) {
            // Success Animated Screen View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Background)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Elevated Green Check Animation Container
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(LightGreen, CircleShape)
                            .border(3.dp, IndianGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success tick",
                            tint = IndianGreen,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Delivery Completed!" else "डिलीवरी संपन्न!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) {
                            "Cash ₹${amountToCollect.toInt()} collected and deposited to your digital wallet for Order $orderId."
                        } else {
                            "ऑर्डर $orderId के लिए ₹${amountToCollect.toInt()} नकद प्राप्त हुआ और आपके डिजिटल वॉलेट में जमा कर दिया गया है।"
                        },
                        fontSize = 15.sp,
                        color = TextVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Back to Dashboard button
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(54.dp)
                            .testTag("success_back_to_dashboard")
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Back to Dashboard" else "डैशबोर्ड पर वापस जाएं",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Main Collection Setup Scroll Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Order Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("order_summary_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Order ID" else "ऑर्डर आईडी",
                                    fontSize = 12.sp,
                                    color = TextVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = orderId,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyBlue
                                )
                            }

                            // COD Badge
                            Box(
                                modifier = Modifier
                                    .background(LightSaffron, RoundedCornerShape(8.dp))
                                    .border(1.dp, Saffron.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "COD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SaffronDark
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = BorderColor.copy(alpha = 0.4f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Total Amount to Collect" else "एकत्र की जाने वाली कुल राशि",
                                fontSize = 14.sp,
                                color = TextVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "₹${amountToCollect.toInt()}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SaffronDark
                            )
                        }
                    }
                }

                // 2. Instructions Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightGreen, RoundedCornerShape(10.dp))
                        .border(1.dp, IndianGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "info icon",
                        tint = IndianGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) {
                            "Hand over the parcel after receiving cash and customer signature."
                        } else {
                            "नकद राशि प्राप्त कर लेने तथा ग्राहक के हस्ताक्षर के बाद ही पार्सल सुपुर्द करें।"
                        },
                        fontSize = 13.sp,
                        color = IndianGreen,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                }

                // 3. Cash Confirmation Button as Checkbox
                Card(
                    onClick = { isCashReceivedCheck = !isCashReceivedCheck },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCashReceivedCheck) LightSaffron else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (isCashReceivedCheck) Saffron else Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cash_received_checkbox_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = isCashReceivedCheck,
                            onCheckedChange = { isCashReceivedCheck = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Saffron,
                                uncheckedColor = BorderColor
                            )
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) {
                                "I have received ₹${amountToCollect.toInt()} in cash"
                            } else {
                                "मुझे नकद भुगतान ₹${amountToCollect.toInt()} प्राप्त हो गया है"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
                        )
                    }
                }

                // 4. Customer Signature Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Customer Digital Signature" else "ग्राहक डिजिटल हस्ताक्षर",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextVariant
                        )
                        Row(
                            modifier = Modifier
                                .clickable {
                                    signatureLines = emptyList()
                                    currentLine = emptyList()
                                }
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "clear signature button",
                                tint = NavyLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Clear" else "साफ़ करें",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyLight
                            )
                        }
                    }

                    // Drawing Canvas Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(BorderStroke(2.dp, BorderColor), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        // Interactive Drawing Board
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentLine = listOf(offset)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            currentLine = currentLine + change.position
                                        },
                                        onDragEnd = {
                                            if (currentLine.isNotEmpty()) {
                                                signatureLines = signatureLines + listOf(currentLine)
                                                currentLine = emptyList()
                                            }
                                        }
                                    )
                                }
                        ) {
                            // Render drawn gestures
                            signatureLines.forEach { line ->
                                if (line.size > 1) {
                                    for (i in 0 until line.size - 1) {
                                        drawLine(
                                            color = TextOnSurface,
                                            start = line[i],
                                            end = line[i + 1],
                                            strokeWidth = 3.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            }

                            // Render currently active gesture path
                            if (currentLine.size > 1) {
                                for (i in 0 until currentLine.size - 1) {
                                    drawLine(
                                        color = TextOnSurface,
                                        start = currentLine[i],
                                        end = currentLine[i + 1],
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }

                        // Finger signature canvas guides/placeholder
                        if (!hasSigned) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Draw,
                                    contentDescription = "Signature Brush",
                                    tint = BorderColor,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Sign here using finger" else "यहाँ उंगली से हस्ताक्षर करें",
                                    fontSize = 12.sp,
                                    color = TextVariant.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
