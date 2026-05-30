package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerLoginScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val lang = viewModel.currentLanguage

    // Multi-language translation support
    val headerTitle = if (lang == LocaleStrings.Lang.EN) "Didi Delivery Partner" else "दीदी डिलीवरी पार्टनर"
    val secureServerText = if (lang == LocaleStrings.Lang.EN) "Secure Server" else "सुरक्षित सर्वर"
    val heroTitle = if (lang == LocaleStrings.Lang.EN) "Partner Login" else "पार्टनर लॉगिन"
    val heroSubtitle = if (lang == LocaleStrings.Lang.EN) "Enter your mobile number to receive a secure OTP" else "सुरक्षित ओटीपी प्राप्त करने के लिए अपना मोबाइल नंबर दर्ज करें"
    val mobileNumberText = if (lang == LocaleStrings.Lang.EN) "Mobile Number" else "मोबाइल नंबर"
    val sendOtpText = if (lang == LocaleStrings.Lang.EN) "Send OTP" else "ओटीपी भेजें"
    val enterOtpText = if (lang == LocaleStrings.Lang.EN) "Enter 6-digit OTP" else "६-अंकीय ओटीपी दर्ज करें"
    val changeNumberText = if (lang == LocaleStrings.Lang.EN) "Change Number" else "नंबर बदलें"
    val verifyLoginText = if (lang == LocaleStrings.Lang.EN) "Verify & Login" else "सत्यापित करें और लॉगिन करें"
    val authenticatingText = if (lang == LocaleStrings.Lang.EN) "Authenticating..." else "प्रमाणित किया जा रहा है..."
    val facingIssuesText = if (lang == LocaleStrings.Lang.EN) "Facing issues? Call Support" else "कोई समस्या है? सपोर्ट को कॉल करें"

    // States
    var mobileNumber by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Mobile Input, 2: OTP Entry
    var isSending by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    // 6-digit OTP States
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // Resend Timer logic
    var timerSeconds by remember { mutableStateOf(59) }
    var isTimerActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerActive) {
        if (isTimerActive) {
            timerSeconds = 59
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
            isTimerActive = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield logo",
                            tint = SaffronDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = headerTitle,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("partner_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyBlue
                        )
                    }
                },
                actions = {
                    // Language Switcher Toggle
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("partner_lang_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle Language",
                            tint = NavyBlue
                        )
                    }

                    // Secure server indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(Color(0xFFF1F4F6), RoundedCornerShape(12.dp))
                            .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(IndianGreen, CircleShape)
                        )
                        Text(
                            text = secureServerText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Subtle, high-key city logistics background overlay aligned at the bottom (opacity 10%)
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC3tpbRYYSXXteuDZ4bF5C-P9SVO3h685YEpkk41wGVUq_cx40AFh8TB1RBqzikiXFLTegKqzUwj5WhNA2Mjw2K6WC1YD8i39Gc_POzQRJ-_MCfKM1nJTRJXGbCRgpZ-FEklkv7t5smxpmxx_jJ8iehSSpJmFvZgeCz6kyV_m2gngg-mRis6T3W1WgzpozoVlaPRHCQdQVyA2YZdLrX9bewXfDOZhYLyYUXjOyMMoGZ2dUh32WCTsUqCEDNn2wz6dvl-kSaym_dxx_O",
                contentDescription = "Urban Delivery vehicle backdrop",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .align(Alignment.BottomCenter)
                    .alpha(0.08f)
            )

            // Scrollable or centered form content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
                    .offset(y = (-40).dp), // adjust visual weight
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Hero visual block
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(LightSaffron, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeliveryDining,
                        contentDescription = "Delivery icon",
                        tint = SaffronDark,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = heroTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextOnSurface
                    )
                    Text(
                        text = if (step == 1) heroSubtitle else "${if (lang == LocaleStrings.Lang.EN) "OTP sent to +91" else "ओटीपी नंबर +91"} $mobileNumber",
                        fontSize = 13.sp,
                        color = TextVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Form Container
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedContent(
                            targetState = step,
                            transitionSpec = {
                                slideInHorizontally { width -> if (targetState > initialState) width else -width } togetherWith
                                        slideOutHorizontally { width -> if (targetState > initialState) -width else width }
                            },
                            label = "Form Step Switcher"
                        ) { targetStep ->
                            if (targetStep == 1) {
                                // STEP 1: MOBILE FORM
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = mobileNumberText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextOnSurface
                                        )

                                        OutlinedTextField(
                                            value = mobileNumber,
                                            onValueChange = { input ->
                                                // Only allow numbers and up to 10 characters
                                                if (input.all { it.isDigit() } && input.length <= 10) {
                                                    mobileNumber = input
                                                }
                                            },
                                            placeholder = {
                                                Text(
                                                    text = "00000 00000",
                                                    color = TextVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            leadingIcon = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                                                ) {
                                                    Text(
                                                        text = "+91",
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextOnSurface,
                                                        fontSize = 15.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .width(1.dp)
                                                            .height(20.dp)
                                                            .background(BorderColor)
                                                    )
                                                }
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Phone,
                                                imeAction = ImeAction.Go
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onGo = {
                                                    if (mobileNumber.length == 10) {
                                                        isSending = true
                                                    }
                                                }
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Saffron,
                                                unfocusedBorderColor = BorderColor,
                                                focusedContainerColor = Background,
                                                unfocusedContainerColor = Background,
                                                focusedTextColor = TextOnSurface,
                                                unfocusedTextColor = TextOnSurface
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("partner_mobile_input")
                                        )
                                    }

                                    // Send OTP Button
                                    Button(
                                        onClick = {
                                            if (mobileNumber.length == 10) {
                                                isSending = true
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    if (lang == LocaleStrings.Lang.EN) "Please enter a valid 10-digit mobile number" else "कृपया १०-अंकीय वैध मोबाइल नंबर दर्ज करें",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        enabled = mobileNumber.length == 10 && !isSending,
                                        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("partner_send_otp_btn")
                                    ) {
                                        if (isSending) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = sendOtpText,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 15.sp
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.ArrowForward,
                                                    contentDescription = "Forward arrow",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // STEP 2: OTP VERIFICATION
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = enterOtpText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextOnSurface
                                        )

                                        Text(
                                            text = changeNumberText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SaffronDark,
                                            modifier = Modifier
                                                .clickable {
                                                    // Reset OTP input
                                                    for (i in 0..5) {
                                                        otpValues[i] = ""
                                                    }
                                                    step = 1
                                                }
                                                .padding(4.dp)
                                        )
                                    }

                                    // Grid of 6 Input Characters
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        for (i in 0..5) {
                                            val value = otpValues[i]
                                            OutlinedTextField(
                                                value = value,
                                                onValueChange = { text ->
                                                    // Only accept 1 number
                                                    val digit = text.filter { it.isDigit() }.takeLast(1)
                                                    otpValues[i] = digit

                                                    // Auto-focus next field
                                                    if (digit.isNotEmpty() && i < 5) {
                                                        focusRequesters[i + 1].requestFocus()
                                                    } else if (digit.isEmpty() && i > 0) {
                                                        focusRequesters[i - 1].requestFocus()
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Saffron,
                                                    unfocusedBorderColor = BorderColor,
                                                    focusedContainerColor = Background,
                                                    unfocusedContainerColor = Background
                                                ),
                                                textStyle = LocalTextStyle.current.copy(
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 20.sp,
                                                    color = TextOnSurface
                                                ),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = if (i == 5) ImeAction.Done else ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onDone = {
                                                        focusManager.clearFocus()
                                                    }
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(56.dp)
                                                    .focusRequester(focusRequesters[i])
                                                    // Intercept hardware key events for backspace logic
                                                    .onKeyEvent { keyEvent ->
                                                        if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace && value.isEmpty() && i > 0) {
                                                            otpValues[i - 1] = ""
                                                            focusRequesters[i - 1].requestFocus()
                                                            true
                                                        } else {
                                                            false
                                                        }
                                                    }
                                                    .testTag("partner_otp_input_$i")
                                            )
                                        }
                                    }

                                    // Focus first box when OTP screen is initialized
                                    LaunchedEffect(Unit) {
                                        focusRequesters[0].requestFocus()
                                    }

                                    // Countdown timer row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val resendLabelPrefix = if (lang == LocaleStrings.Lang.EN) "Resend in" else "पुनः भेजें"
                                        Text(
                                            text = if (timerSeconds > 0) "$resendLabelPrefix 00:${"%02d".format(timerSeconds)}" else if (lang == LocaleStrings.Lang.EN) "Didn't receive OTP?" else "ओटीपी नहीं मिला?",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextVariant
                                        )

                                        val resendText = if (lang == LocaleStrings.Lang.EN) "Resend OTP" else "ओटीपी पुनः भेजें"
                                        Text(
                                            text = resendText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (timerSeconds == 0) SaffronDark else TextVariant.copy(alpha = 0.5f),
                                            modifier = Modifier
                                                .clickable(enabled = timerSeconds == 0) {
                                                    timerSeconds = 59
                                                    isTimerActive = true
                                                    Toast.makeText(
                                                        context,
                                                        if (lang == LocaleStrings.Lang.EN) "OTP Resent successfully!" else "ओटीपी पुनः भेजा गया!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }

                                    val isOtpComplete = otpValues.all { it.isNotEmpty() }

                                    // Verify Button styled based on completeness
                                    Button(
                                        onClick = {
                                            if (isOtpComplete) {
                                                isVerifying = true
                                            }
                                        },
                                        enabled = isOtpComplete && !isVerifying,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isOtpComplete) Saffron else Color(0xFFD7DADC),
                                            disabledContainerColor = Color(0xFFD7DADC)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("partner_verify_btn")
                                    ) {
                                        if (isVerifying) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                CircularProgressIndicator(
                                                    color = NavyBlue,
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp
                                                )
                                                Text(
                                                    text = authenticatingText,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyBlue,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        } else {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isOtpComplete) Icons.Default.LockOpen else Icons.Default.Lock,
                                                    contentDescription = "Lock icon",
                                                    tint = if (isOtpComplete) Color.White else TextVariant.copy(alpha = 0.6f)
                                                )
                                                Text(
                                                    text = verifyLoginText,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isOtpComplete) Color.White else TextVariant.copy(alpha = 0.6f),
                                                    fontSize = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Facing Issues Call Support row
                Row(
                    modifier = Modifier
                        .clickable {
                            Toast.makeText(
                                context,
                                if (lang == LocaleStrings.Lang.EN) "Calling Logistics Support +91-1800-DIDI-LOG" else "लॉजिस्टिक्स सपोर्ट +91-1800-DIDI-LOG पर कॉल कर रहे हैं",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Support agent",
                        tint = NavyLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = facingIssuesText,
                        fontSize = 13.sp,
                        color = NavyLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                
                // Footer brand note
                Text(
                    text = "POWERED BY DIDI LOGISTICS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextVariant.copy(alpha = 0.5f),
                    letterSpacing = 1.5.sp
                )
            }
        }
    }

    // Handle Mock Delay and transitions
    LaunchedEffect(isSending) {
        if (isSending) {
            delay(1200) // simulated network lag
            isSending = false
            step = 2
            isTimerActive = true
            Toast.makeText(
                context,
                if (lang == LocaleStrings.Lang.EN) "OTP sent successfully to +91 $mobileNumber" else "+91 $mobileNumber पर सफलतापूर्वक ओटीपी भेजा गया!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(isVerifying) {
        if (isVerifying) {
            delay(1500) // simulated server authentication delay
            isVerifying = false
            Toast.makeText(
                context,
                if (lang == LocaleStrings.Lang.EN) "Login successful! Welcome to Logistics Dashboard!" else "लॉगिन सफल! लॉजिस्टिक्स डैशबोर्ड पर आपका स्वागत है!",
                Toast.LENGTH_LONG
            ).show()
            
            // Route directly to the delivery partner dashboard
            viewModel.showPartnerLoginScreen = false
            viewModel.showPartnerDashboard = true
        }
    }
}
