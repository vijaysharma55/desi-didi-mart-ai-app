package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerDashboardScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lang = viewModel.currentLanguage

    // Active bottom navigation tab: "deliveries", "cod", "earnings", "profile"
    var activeDashboardTab by remember { mutableStateOf("deliveries") }
    var isOnline by remember { mutableStateOf(true) }
    var showDrawerMenu by remember { mutableStateOf(false) }

    // Navigation and delivery progress states
    var isNavigating by remember { mutableStateOf(false) }
    var rideProgress by remember { mutableStateOf(0.15f) } // Animation progress of rider
    val animatedProgress by animateFloatAsState(
        targetValue = rideProgress,
        animationSpec = tween(durationMillis = 3500, easing = LinearEasing),
        label = "Rider Movement"
    )

    // Handle distance countdown as rider approaches
    val remainingDistance = remember(animatedProgress) {
        val dist = 2.4 * (1.0f - animatedProgress)
        if (dist < 0.1) 0.1 else dist
    }
    val etaMinutes = remember(remainingDistance) {
        val mins = (remainingDistance * 3.5).toInt()
        if (mins < 1) 1 else mins
    }

    // Modal dialogs
    var showCallDialog by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var showVerificationSuccess by remember { mutableStateOf(false) }

    // Multi-Language keys
    val pTitle = if (lang == LocaleStrings.Lang.EN) "Didi Delivery Partner" else "दीदी डिलीवरी पार्टनर"
    val profileName = if (lang == LocaleStrings.Lang.EN) "Karan Kumar" else "करण कुमार"
    val orderIdText = "ID: #DIDI-28492"
    val customerName = if (lang == LocaleStrings.Lang.EN) "Ramesh Singh" else "रमेश सिंह"
    val customerPos = if (lang == LocaleStrings.Lang.EN) "Flat 402, Sunshine Apts, Dwarka" else "फ्लैट ४०२, सनशाइन अपार्ट्स, द्वारका"

    // Set milestone based on state variables
    LaunchedEffect(viewModel.partnerMilestoneStep) {
        if (viewModel.partnerMilestoneStep == 2) {
            rideProgress = 1.0f // Rider reaches destination
        }
    }

    if (showDrawerMenu) {
        ModalBottomSheet(
            onDismissRequest = { showDrawerMenu = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(LightSaffron, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "Rider icon",
                            tint = SaffronDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = profileName,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Vehicle: Electric Scooter" else "वाहन: इलेक्ट्रिक स्कूटर",
                            color = TextVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                HorizontalDivider(color = BorderColor.copy(alpha = 0.4f))

                // Drawer Links
                DrawerMenuItem(
                    icon = Icons.Default.Schedule,
                    title = if (lang == LocaleStrings.Lang.EN) "Today's Shift: 8:00 AM - 6:00 PM" else "आज की शिफ्ट: सुबह ८:०० - शाम ६:००",
                    onClick = { showDrawerMenu = false }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Rule,
                    title = if (lang == LocaleStrings.Lang.EN) "Safety Checklist Verified" else "सुरक्षा चेकलिस्ट सत्यापित",
                    onClick = { showDrawerMenu = false }
                )
                DrawerMenuItem(
                    icon = Icons.Default.PhoneCallback,
                    title = if (lang == LocaleStrings.Lang.EN) "Logistics Helpline Desk" else "लॉजिस्टिक्स हेल्प डेस्क",
                    onClick = {
                        Toast.makeText(context, "Logistics help channel opened", Toast.LENGTH_SHORT).show()
                        showDrawerMenu = false
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        showDrawerMenu = false
                        viewModel.showPartnerDashboard = false
                        Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Logged out of portal" else "पोर्टल से बाहर निकले", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Exit Logistics Dashboard" else "लॉजिस्टिक्स डैशबोर्ड से बाहर निकलें",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            confirmButton = {
                Button(
                    onClick = { showCallDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = IndianGreen)
                ) {
                    Text(text = if (lang == LocaleStrings.Lang.EN) "End Call" else "कॉल समाप्त करें", color = Color.White)
                }
            },
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Calling Customer" else "ग्राहक को कॉल",
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextOnSurface
                    )
                    Text(
                        text = "+91-98765-XXXXX",
                        color = TextVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // simulated call animation pulsing dot
                    val pulseTransition = rememberInfiniteTransition(label = "Pulse")
                    val pulseScale by pulseTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "PulseScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(LightGreen, CircleShape)
                            .border(2.dp, IndianGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Calling",
                            tint = IndianGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Connecting via secure bridge..." else "सुरक्षित नंबर ब्रिज द्वारा कॉल जुड़ रहा है...",
                        fontSize = 12.sp,
                        color = TextVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            containerColor = Color.White
        )
    }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (otpInput == "4590" || otpInput == "1234" || otpInput.length == 4) {
                            showOtpDialog = false
                            viewModel.activeCashCollectionOrderId = "#DD-8821"
                        } else {
                            Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Incorrect delivery OTP! Customer OTP is 4590" else "गलत डिलीवरी ओटीपी! ग्राहक कोड 4590 है", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                    modifier = Modifier.testTag("partner_submit_delivery_otp")
                ) {
                    Text(text = if (lang == LocaleStrings.Lang.EN) "Verify" else "सत्यापित करें", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text(text = if (lang == LocaleStrings.Lang.EN) "Cancel" else "रद्द करें", color = TextVariant)
                }
            },
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Enter Customer OTP" else "ग्राहक सुरक्षा ओटीपी डालें",
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Ask customer for the 4-digit code shown in their active order description (Tip: Use 4590 to proceed)." else "ग्राहक से ४-अंकीय कोड पूछें जो उनके सक्रिय आर्डर पर है (संकेत: आगे बढ़ने हेतु 4590 टाइप करें)।",
                        fontSize = 13.sp,
                        color = TextVariant
                    )

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { if (it.all { ch -> ch.isDigit() } && it.length <= 4) otpInput = it },
                        placeholder = { Text(text = "0 0 0 0", color = TextVariant.copy(alpha = 0.4f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { }),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyBlue
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Saffron,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = Background,
                            unfocusedContainerColor = Background
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("partner_delivery_otp_input")
                    )
                }
            },
            containerColor = Color.White
        )
    }

    if (showVerificationSuccess) {
        AlertDialog(
            onDismissRequest = {  },
            confirmButton = {
                Button(
                    onClick = {
                        showVerificationSuccess = false
                        viewModel.isPartnerOrderDelivered = true
                        viewModel.partnerDeliveriesCount += 1
                        viewModel.partnerDistanceKms += 2.4
                        viewModel.partnerMilestoneStep = 3 // Finished
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndianGreen),
                    modifier = Modifier.testTag("partner_dismiss_success_alert")
                ) {
                    Text(text = if (lang == LocaleStrings.Lang.EN) "Good Work!" else "बहुत बढ़िया!", color = Color.White)
                }
            },
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Delivery Successful!" else "डिलीवरी सफल रही!",
                    fontWeight = FontWeight.Bold,
                    color = IndianGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(LightGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success Confetti",
                            tint = IndianGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Earnings of ₹45 for Order #DIDI-28492 added to your wallet wallet." else "ऑर्डर #DIDI-28492 के लिए ₹45 आपकी आय में जोड़ दिए गए हैं।",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = TextOnSurface
                    )

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Bonus of +20 Didi Coins loyalty points rewarded!" else "+20 देशी दीदी कॉइन्स लॉयल्टी बोनस मिला!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            containerColor = Color.White
        )
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
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Drawer",
                            tint = NavyBlue,
                            modifier = Modifier
                                .clickable { showDrawerMenu = true }
                                .padding(8.dp)
                                .testTag("partner_drawer_icon")
                        )
                        Text(
                            text = pTitle,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyBlue,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    // Pulse "Online" Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clickable {
                                isOnline = !isOnline
                                val stateString = if (isOnline) "ONLINE" else "OFFLINE"
                                Toast.makeText(context, "Status set to $stateString", Toast.LENGTH_SHORT).show()
                            }
                            .background(
                                if (isOnline) LightGreen else Color(0xFFFFDAD6),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (isOnline) IndianGreen.copy(alpha = 0.4f) else ErrorColor.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("partner_online_toggle")
                    ) {
                        val pulseTransition = rememberInfiniteTransition(label = "OnlinePulse")
                        val dotAlpha by pulseTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "DotAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .alpha(if (isOnline) dotAlpha else 1.0f)
                                .background(if (isOnline) IndianGreen else ErrorColor, CircleShape)
                        )

                        Text(
                            text = if (isOnline) {
                                if (lang == LocaleStrings.Lang.EN) "Online" else "सक्रिय"
                            } else {
                                if (lang == LocaleStrings.Lang.EN) "Offline" else "निष्क्रिय"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) IndianGreen else ErrorColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Language toggle inside map portal
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Icon(imageVector = Icons.Default.Translate, contentDescription = "Language", tint = NavyBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // High comfort Material 3 Navigation Bar matching HTML tabs
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeDashboardTab == "deliveries",
                    onClick = { activeDashboardTab = "deliveries" },
                    icon = { Icon(imageVector = Icons.Default.LocalShipping, contentDescription = "Deliveries") },
                    label = { Text(text = if (lang == LocaleStrings.Lang.EN) "Deliveries" else "डिलीवरी", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SaffronDark,
                        unselectedIconColor = TextVariant.copy(alpha = 0.7f),
                        unselectedTextColor = TextVariant.copy(alpha = 0.7f),
                        indicatorColor = Saffron
                    ),
                    modifier = Modifier.testTag("nav_partner_deliveries")
                )

                NavigationBarItem(
                    selected = activeDashboardTab == "cod",
                    onClick = { activeDashboardTab = "cod" },
                    icon = { Icon(imageVector = Icons.Default.Payments, contentDescription = "COD") },
                    label = { Text(text = "COD", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SaffronDark,
                        unselectedIconColor = TextVariant.copy(alpha = 0.7f),
                        unselectedTextColor = TextVariant.copy(alpha = 0.7f),
                        indicatorColor = Saffron
                    ),
                    modifier = Modifier.testTag("nav_partner_cod")
                )

                NavigationBarItem(
                    selected = activeDashboardTab == "earnings",
                    onClick = { activeDashboardTab = "earnings" },
                    icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Earnings") },
                    label = { Text(text = if (lang == LocaleStrings.Lang.EN) "Earnings" else "कमाई", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SaffronDark,
                        unselectedIconColor = TextVariant.copy(alpha = 0.7f),
                        unselectedTextColor = TextVariant.copy(alpha = 0.7f),
                        indicatorColor = Saffron
                    ),
                    modifier = Modifier.testTag("nav_partner_earnings")
                )

                NavigationBarItem(
                    selected = activeDashboardTab == "profile",
                    onClick = { activeDashboardTab = "profile" },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(text = if (lang == LocaleStrings.Lang.EN) "Profile" else "प्रोफ़ाइल", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SaffronDark,
                        unselectedIconColor = TextVariant.copy(alpha = 0.7f),
                        unselectedTextColor = TextVariant.copy(alpha = 0.7f),
                        indicatorColor = Saffron
                    ),
                    modifier = Modifier.testTag("nav_partner_profile")
                )
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeDashboardTab) {
                "deliveries" -> {
                    // MAIN DELIVERIES TAB DRAWINGS MAP & STATS
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Live simulated street map container (Bento layout style)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color(0xFFE2E6E9))
                                .border(1.dp, BorderColor.copy(alpha = 0.6f))
                        ) {
                            // Street Map Art Canvas
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // Draw simulated Green Parks / Sectors
                                drawRect(
                                    color = Color(0xFFAED581).copy(alpha = 0.4f),
                                    topLeft = Offset(40f, 30f),
                                    size = Size(canvasWidth * 0.25f, canvasHeight * 0.35f)
                                )

                                drawRect(
                                    color = Color(0xFFAED581).copy(alpha = 0.35f),
                                    topLeft = Offset(canvasWidth * 0.7f, canvasHeight * 0.5f),
                                    size = Size(canvasWidth * 0.25f, canvasHeight * 0.4f)
                                )

                                // Draw Water Body
                                drawCircle(
                                    color = Color(0xFF81D4FA).copy(alpha = 0.40f),
                                    radius = 70f,
                                    center = Offset(canvasWidth * 0.85f, 100f)
                                )

                                // Draw road layout networks
                                val roadStroke = Stroke(
                                    width = 24f,
                                    pathEffect = null
                                )
                                val roadMutedColor = Color(0xFFFFFFFF)

                                // Road 1 Horizontal main
                                drawLine(
                                    color = roadMutedColor,
                                    start = Offset(0f, canvasHeight * 0.4f),
                                    end = Offset(canvasWidth, canvasHeight * 0.4f),
                                    strokeWidth = 32f
                                )

                                // Road 2 Vertical Main
                                drawLine(
                                    color = roadMutedColor,
                                    start = Offset(canvasWidth * 0.42f, 0f),
                                    end = Offset(canvasWidth * 0.42f, canvasHeight),
                                    strokeWidth = 32f
                                )

                                // Sub Diagonal connection road
                                drawLine(
                                    color = roadMutedColor,
                                    start = Offset(60f, 60f),
                                    end = Offset(canvasWidth * 0.42f, canvasHeight * 0.4f),
                                    strokeWidth = 24f
                                )

                                drawLine(
                                    color = roadMutedColor,
                                    start = Offset(canvasWidth * 0.42f, canvasHeight * 0.4f),
                                    end = Offset(canvasWidth - 100f, canvasHeight - 60f),
                                    strokeWidth = 24f
                                )

                                // Draw Dotted delivery active route trace path
                                val routePath = Path().apply {
                                    moveTo(80f, 80f) // start location hub
                                    lineTo(canvasWidth * 0.42f, canvasHeight * 0.4f) // junction
                                    lineTo(canvasWidth * 0.7f, canvasHeight * 0.65f) // halfway
                                    lineTo(canvasWidth - 150f, canvasHeight - 110f) // destination Sunshine Apts
                                }

                                drawPath(
                                    path = routePath,
                                    color = NavyLight,
                                    style = Stroke(
                                        width = 8f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                    )
                                )

                                // Markers Placement
                                // 1. DIDI Shop Hub (Start)
                                drawCircle(
                                    color = Saffron,
                                    radius = 16f,
                                    center = Offset(80f, 80f)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 7f,
                                    center = Offset(80f, 80f)
                                )

                                // 2. Destination Apartments flat (End)
                                drawCircle(
                                    color = ErrorColor,
                                    radius = 18f,
                                    center = Offset(canvasWidth - 150f, canvasHeight - 110f)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 8f,
                                    center = Offset(canvasWidth - 150f, canvasHeight - 110f)
                                )

                                // 3. RIDER Location marker along path using the animated progress coefficient
                                // interpolate along the path sections
                                val p1X = 80f
                                val p1Y = 80f
                                val p2X = canvasWidth * 0.42f
                                val p2Y = canvasHeight * 0.4f
                                val p3X = canvasWidth - 150f
                                val p3Y = canvasHeight - 110f

                                val rx: Float
                                val ry: Float
                                if (animatedProgress < 0.5f) {
                                    val localSegProg = animatedProgress / 0.5f
                                    rx = p1X + localSegProg * (p2X - p1X)
                                    ry = p1Y + localSegProg * (p2Y - p1Y)
                                } else {
                                    val localSegProg = (animatedProgress - 0.5f) / 0.5f
                                    rx = p2X + localSegProg * (p3X - p2X)
                                    ry = p2Y + localSegProg * (p3Y - p2Y)
                                }

                                // Render glowing active rider dot
                                drawCircle(
                                    color = NavyBlue.copy(alpha = 0.35f),
                                    radius = 24f,
                                    center = Offset(rx, ry)
                                )
                                drawCircle(
                                    color = NavyBlue,
                                    radius = 12f,
                                    center = Offset(rx, ry)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 5f,
                                    center = Offset(rx, ry)
                                )
                            }

                            // GPS location button on Map overlays
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            rideProgress = 0.15f // Reset rider
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (lang == LocaleStrings.Lang.EN) "Simulated GPS tracker centered" else "सिम्युलेटेड जीपीएस ट्रैकर केंद्रित",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Locate Me", tint = NavyBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            // Dynamic Live status ribbon overlay on bottom map
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(NavyLight.copy(alpha = 0.9f))
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NearMe,
                                            contentDescription = "Routing info",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (viewModel.isPartnerOrderDelivered) {
                                                if (lang == LocaleStrings.Lang.EN) "Delivery completed successfully" else "डिलीवरी सफलतापूर्वक पूरी हुई"
                                            } else if (viewModel.partnerMilestoneStep == 2) {
                                                if (lang == LocaleStrings.Lang.EN) "Arrived at Customer location" else "ग्राहक के स्थान पर पहुंचे"
                                            } else {
                                                if (lang == LocaleStrings.Lang.EN) "En route to Ramesh Singh..." else "रमेश सिंह के घर की तरफ एन-रूट..."
                                            },
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    if (!viewModel.isPartnerOrderDelivered) {
                                        Text(
                                            text = "${"%.1f".format(remainingDistance)} km • $etaMinutes mins",
                                            color = Saffron,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Active Delivery Detail Card slightly overlapping (Card - Bento style)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            if (!viewModel.isPartnerOrderDelivered) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(3.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column {
                                                Text(
                                                    text = orderIdText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyLight,
                                                    letterSpacing = 1.sp
                                                )

                                                Text(
                                                    text = customerName,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = TextOnSurface
                                                )
                                            }

                                            // Distance Badge
                                            Box(
                                                modifier = Modifier
                                                    .background(LightSaffron, RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "${"%.1f".format(remainingDistance)} km away",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SaffronDark
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = "Pin",
                                                tint = ErrorColor,
                                                modifier = Modifier.size(20.dp)
                                            )

                                            Text(
                                                text = customerPos + ", flat 402, sector 12, Dwarka, New Delhi 110075",
                                                fontSize = 13.sp,
                                                color = TextVariant,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Action buttons "Call customer" and "Navigate"
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { showCallDialog = true },
                                                border = BorderStroke(1.dp, NavyLight.copy(alpha = 0.7f)),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .testTag("partner_call_customer")
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                                    Text(text = if (lang == LocaleStrings.Lang.EN) "Call Customer" else "ग्राहक को कॉल", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    isNavigating = true
                                                    rideProgress = 1.0f // triggers animated approach
                                                    viewModel.partnerMilestoneStep = 2 // arrived
                                                    Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "GPS Navigation Started..." else "जीपीएस नेविगेशन प्रारंभ हुआ...", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .weight(1.5f)
                                                    .height(44.dp)
                                                    .testTag("partner_simulate_nav")
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(imageVector = Icons.Default.Navigation, contentDescription = "Nav", modifier = Modifier.size(16.dp))
                                                    Text(text = if (lang == LocaleStrings.Lang.EN) "Simulate Route" else "मार्ग अनुकरण करें", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // ORDER COMPLETED STATUS STATE
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                                    border = BorderStroke(1.dp, IndianGreen.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Completed", tint = IndianGreen, modifier = Modifier.size(44.dp))
                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "All Deliveries Completed!" else "सभी डिलीवरी पूरी हो चुकी हैं!",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IndianGreen
                                        )
                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "You delivered Ramesh's Tuvar Dal successfully. Standby for new orders!" else "आपने रमेश की तुवर दाल सफलतापूर्वक डिलीवर कर दी है। नए ऑर्डर की प्रतीक्षा करें!",
                                            fontSize = 13.sp,
                                            color = IndianGreen,
                                            textAlign = TextAlign.Center
                                        )

                                        Button(
                                            onClick = {
                                                // Reset demo loop
                                                viewModel.isPartnerOrderDelivered = false
                                                viewModel.partnerMilestoneStep = 1
                                                rideProgress = 0.15f
                                                isNavigating = false
                                                otpInput = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .height(36.dp)
                                                .testTag("partner_reset_demo_btn")
                                        ) {
                                            Text(text = if (lang == LocaleStrings.Lang.EN) "Reset Order Demo" else "ऑर्डर डेमो रीसेट करें", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Dual Statistics Bento Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Card 1: Deliveries Today
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = "Stats delivery",
                                        tint = NavyLight,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "Deliveries Today" else "आज की डिलीवरी",
                                            fontSize = 11.sp,
                                            color = TextVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "%02d".format(viewModel.partnerDeliveriesCount),
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SaffronDark
                                        )
                                    }
                                }
                            }

                            // Card 2: Distance Travelled
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = "Stats distance",
                                        tint = SaffronDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "Distance Travelled" else "कुल तय की गई दूरी",
                                            fontSize = 11.sp,
                                            color = TextVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "${"%.1f".format(viewModel.partnerDistanceKms)}",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SaffronDark
                                            )
                                            Text(
                                                text = "km",
                                                fontSize = 14.sp,
                                                color = TextVariant,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Milestone Vertical Stepper Checklist
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Milestones Checklist" else "महत्वपूर्ण उपलब्धियां",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue
                                )

                                // Milestone Step 1: Picked Up
                                MilestoneRow(
                                    index = 1,
                                    text = if (lang == LocaleStrings.Lang.EN) "Order Picked Up from Store" else "ऑर्डर स्टोर से ले लिया गया है",
                                    currentStep = viewModel.partnerMilestoneStep,
                                    isDone = viewModel.partnerMilestoneStep >= 1
                                )

                                // Milestone Step 2: En Route / Arrived
                                MilestoneRow(
                                    index = 2,
                                    text = if (lang == LocaleStrings.Lang.EN) "Arrive at Customer Location" else "ग्राहक के स्थान पर पहुँचना",
                                    currentStep = viewModel.partnerMilestoneStep,
                                    isDone = viewModel.partnerMilestoneStep >= 2
                                )

                                // Milestone Step 3: Complete Delivery
                                MilestoneRow(
                                    index = 3,
                                    text = if (lang == LocaleStrings.Lang.EN) "Verify customer OTP & Complete" else "ग्राहक सुरक्षा कोड सत्यापित और पूर्ण करें",
                                    currentStep = viewModel.partnerMilestoneStep,
                                    isDone = viewModel.partnerMilestoneStep >= 3,
                                    isLast = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5. floating context delivery trigger
                        if (!viewModel.isPartnerOrderDelivered) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (viewModel.partnerMilestoneStep == 1) {
                                            // Simulate arrival
                                            viewModel.partnerMilestoneStep = 2
                                            rideProgress = 1.0f
                                            Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Welcome! You have arrived. Code is 4590" else "स्वागतम्! आप आर्डर स्थान पर पहुँच गए हैं। ओटीपी: 4590", Toast.LENGTH_LONG).show()
                                        } else if (viewModel.partnerMilestoneStep == 2) {
                                            showOtpDialog = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("partner_primary_action_btn")
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Action complete",
                                            tint = Color.White
                                        )

                                        Text(
                                            text = if (viewModel.partnerMilestoneStep == 1) {
                                                if (lang == LocaleStrings.Lang.EN) "Arrived at Customer Location" else "स्थान पर आगमन"
                                            } else {
                                                if (lang == LocaleStrings.Lang.EN) "Verify Code & Handover" else "सुरक्षा ओटीपी सत्यापित करें"
                                            },
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                "cod" -> {
                    // COD (CASH ON DELIVERY) TAB
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                // Bento Header Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NavyBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "CASH IN HAND" else "हाथ में नकद राशि",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )

                                        val currentCashSum = if (viewModel.isPartnerOrderDelivered) "₹2,090.00" else "₹1,240.00"
                                        Text(
                                            text = currentCashSum,
                                            color = Color.White,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )

                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "Submit cash collected to Dwarka central hub by 8:00 PM" else "शाम ८:०० बजे तक द्वारका मुख्य हब पर नकद जमा करें",
                                            color = Saffron,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Pending Collections Today" else "आज के लंबित संग्रह",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = NavyBlue
                                )
                            }

                            items(4) { index ->
                                val orders = listOf(
                                    Triple("#DD-8821", if (lang == LocaleStrings.Lang.EN) "Ramesh Singh" else "रमेश सिंह", "₹850"),
                                    Triple("#DIDI-28401", "Meera Devi", "₹450"),
                                    Triple("#DIDI-28415", "Amit Sharma", "₹540"),
                                    Triple("#DIDI-28430", "Pushpa Roy", "₹250")
                                )
                                val tuple = orders[index]
                                val isFocusOrder = tuple.first == "#DD-8821"
                                val isDelivered = if (isFocusOrder) viewModel.isPartnerOrderDelivered else true

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isFocusOrder && !isDelivered) {
                                                viewModel.activeCashCollectionOrderId = "#DD-8821"
                                            } else if (isFocusOrder) {
                                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Cash ₹850 already collected for order #DD-8821" else "ऑर्डर #DD-8821 के लिए ₹850 पहले ही एकत्र किए जा चुके हैं", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Cash already collected for ${tuple.second}" else "${tuple.second} के लिए नकद पहले ही एकत्र किया जा चुका है", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .testTag("cod_order_card_${tuple.first.replace("#", "")}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = "ID: ${tuple.first}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextVariant
                                            )
                                            Text(
                                                text = tuple.second,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextOnSurface
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = tuple.third,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isDelivered) IndianGreen else ErrorColor
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (isDelivered) LightGreen else ErrorContainer,
                                                        CircleShape
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (isDelivered) {
                                                        if (lang == LocaleStrings.Lang.EN) "Collected" else "प्राप्त"
                                                    } else {
                                                        if (lang == LocaleStrings.Lang.EN) "Pending" else "लंबित"
                                                    },
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDelivered) IndianGreen else ErrorColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Cash handover request sent to manager!" else "नकद हैंडओवर अनुरोध प्रबंधक को भेजा गया!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("submit_cod_btn")
                                ) {
                                    Text(
                                        text = if (lang == LocaleStrings.Lang.EN) "Initiate Cash Handover at Hub" else "हब पर कैश हैंडओवर शुरू करें",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                "earnings" -> {
                    // NEW EARNINGS TAB WITH INTEGRATED BENTO GRID, ANIMATED CHARTS, AND DETAILED TRANSACTIONS
                    var isDailySelected by remember { mutableStateOf(true) }
                    var chartAnimatedScale by remember { mutableStateOf(0f) }

                    LaunchedEffect(isDailySelected) {
                        chartAnimatedScale = 0f
                        delay(50)
                        chartAnimatedScale = 1f
                    }

                    val animatedScale by animateFloatAsState(
                        targetValue = chartAnimatedScale,
                        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
                        label = "BarChartAnimation"
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 1. Bento Grid - Row 1: Total Earnings Today
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001C3A)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("earnings_today_bento_card")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.08f),
                                            modifier = Modifier
                                                .size(100.dp)
                                                .align(Alignment.CenterEnd)
                                        )

                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) "Total Earnings Today" else "आज की कुल कमाई",
                                                fontSize = 14.sp,
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "₹1,240",
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Saffron
                                            )
                                        }
                                    }
                                }
                            }

                            // Bento Grid - Row 2: Deliveries Counter and Online Time Side-by-Side
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("earnings_deliveries_bento")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) "Deliveries" else "डिलीवरी",
                                                fontSize = 13.sp,
                                                color = TextVariant,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                val deliveriesCount = if (viewModel.isPartnerOrderDelivered) 12 else 11
                                                Text(
                                                    text = "$deliveriesCount",
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = NavyBlue
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.LocalShipping,
                                                    contentDescription = null,
                                                    tint = NavyBlue.copy(alpha = 0.3f),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("earnings_online_time_bento")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) "Online Time" else "ऑनलाइन समय",
                                                fontSize = 13.sp,
                                                color = TextVariant,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                Text(
                                                    text = "6.5h",
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = NavyBlue
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = NavyBlue.copy(alpha = 0.3f),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. Interactive Charts Section & Toggle
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("earnings_trends_chart_card")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) "Earnings Trend" else "कमाई का रुझान",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = NavyBlue
                                            )

                                            // Dual toggle badge pill
                                            Row(
                                                modifier = Modifier
                                                    .background(Color(0xFFF1F4F6), RoundedCornerShape(20.dp))
                                                    .padding(2.dp)
                                            ) {
                                                // Daily button
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(18.dp))
                                                        .background(if (isDailySelected) Color(0xFF001C3A) else Color.Transparent)
                                                        .clickable { isDailySelected = true }
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) "Daily" else "दैनिक",
                                                        color = if (isDailySelected) Color.White else TextVariant,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                // Weekly button
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(18.dp))
                                                        .background(if (!isDailySelected) Color(0xFF001C3A) else Color.Transparent)
                                                        .clickable { isDailySelected = false }
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) "Weekly" else "साप्ताहिक",
                                                        color = if (!isDailySelected) Color.White else TextVariant,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Beautiful Custom Vector-scaled Column Bar Graph
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            val graphItems = if (isDailySelected) {
                                                listOf(
                                                    EarningsGraphItem(if (lang == LocaleStrings.Lang.EN) "Mon" else "सोम", 420.0, 0.40f, false),
                                                    EarningsGraphItem(if (lang == LocaleStrings.Lang.EN) "Tue" else "मंगल", 680.0, 0.65f, false),
                                                    EarningsGraphItem(if (lang == LocaleStrings.Lang.EN) "Wed" else "बुध", 590.0, 0.55f, false),
                                                    EarningsGraphItem(if (lang == LocaleStrings.Lang.EN) "Thu" else "गुरु", 1240.0, 0.90f, true),
                                                    EarningsGraphItem(if (lang == LocaleStrings.Lang.EN) "Fri" else "शुक्र", 0.0, 0.10f, false, isUpcoming = true)
                                                )
                                            } else {
                                                listOf(
                                                    EarningsGraphItem("Wk 1", 3800.0, 0.45f, false),
                                                    EarningsGraphItem("Wk 2", 4500.0, 0.60f, false),
                                                    EarningsGraphItem("Wk 3", 5120.0, 0.72f, false),
                                                    EarningsGraphItem("Wk 4", 6240.0, 0.90f, true),
                                                    EarningsGraphItem("Wk 5", 0.0, 0.15f, false, isUpcoming = true)
                                                )
                                            }

                                            graphItems.forEach { item ->
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .alpha(if (item.isUpcoming) 0.4f else 1.0f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier.height(20.dp),
                                                        contentAlignment = Alignment.BottomCenter
                                                    ) {
                                                        if (item.value > 0) {
                                                            Text(
                                                                text = if (isDailySelected) "₹${item.value.toInt()}" else "₹${String.format("%.1fk", item.value / 1000.0)}",
                                                                fontSize = 10.sp,
                                                                fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.Medium,
                                                                color = if (item.isActive) SaffronDark else TextVariant
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Box(
                                                        modifier = Modifier
                                                            .width(28.dp)
                                                            .height(110.dp * item.percentage * animatedScale)
                                                            .background(
                                                                color = if (item.isActive) Saffron else Color(0xFFE0E3E5),
                                                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                                            )
                                                    )

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Text(
                                                        text = item.label,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (item.isActive) TextOnSurface else TextVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. Payout Status Card
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F6)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("earnings_payout_status_card")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min)
                                    ) {
                                        // Vertical left Saffron border strip of 5.dp width
                                        Box(
                                            modifier = Modifier
                                                .width(5.dp)
                                                .fillMaxHeight()
                                                .background(Saffron)
                                        )

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column {
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) "Next Payout" else "अगला भुगतान",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp,
                                                        color = Color(0xFF001C3A)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) "Friday, 20 Oct" else "शुक्रवार, २० अक्टूबर",
                                                        fontSize = 14.sp,
                                                        color = TextVariant
                                                    )
                                                }

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) "To: HDFC Bank" else "प्रति: एचडीएफसी बैंक",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextOnSurface
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) "A/C: ****4492" else "खाता: ****४४९२",
                                                        fontSize = 11.sp,
                                                        color = TextVariant.copy(alpha = 0.7f)
                                                    )
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VerifiedUser,
                                                    contentDescription = "verified badge",
                                                    tint = IndianGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = if (lang == LocaleStrings.Lang.EN) "Ready for transfer" else "स्थानांतरण के लिए तैयार",
                                                    color = IndianGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 4. List of Recent Deliveries
                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (lang == LocaleStrings.Lang.EN) "Recent Deliveries" else "हाल की डिलीवरी",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyBlue
                                        )

                                        TextButton(
                                            onClick = {
                                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Showing all recent deliveries" else "सभी हालिया डिलीवरी दिखाई जा रही हैं", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) "See All" else "सभी देखें",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SaffronDark
                                            )
                                        }
                                    }

                                    // List of Delivery Items from Mockup
                                    val recentList = listOf(
                                        Triple("#DD-7721", if (lang == LocaleStrings.Lang.EN) "Today, 2:45 PM" else "आज, दोपहर २:४५ बजे", Triple("₹55", "₹45 + ₹10 bonus", "₹४५ + ₹१० बोनस")),
                                        Triple("#DD-7690", if (lang == LocaleStrings.Lang.EN) "Today, 1:12 PM" else "आज, दोपहर १:१२ बजे", Triple("₹42", "Regular Fare", "नियमित किराया")),
                                        Triple("#DD-7655", if (lang == LocaleStrings.Lang.EN) "Today, 12:30 PM" else "आज, दोपहर १२:३० बजे", Triple("₹68", "Long distance bonus", "लंबी दूरी का बोनस"))
                                    )

                                    recentList.forEach { (orderId, time, fareGroup) ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("earnings_recent_item_$orderId")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(44.dp)
                                                            .background(LightSaffron, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.LocalShipping,
                                                            contentDescription = null,
                                                            tint = SaffronDark,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }

                                                    Column {
                                                        Text(
                                                            text = "Order $orderId",
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = TextOnSurface
                                                        )
                                                        Text(
                                                            text = time,
                                                            fontSize = 12.sp,
                                                            color = TextVariant
                                                        )
                                                    }
                                                }

                                                Column(
                                                    horizontalAlignment = Alignment.End
                                                ) {
                                                    Text(
                                                        text = fareGroup.first,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextOnSurface
                                                    )
                                                    Text(
                                                        text = if (lang == LocaleStrings.Lang.EN) fareGroup.second else fareGroup.third,
                                                        fontSize = 11.sp,
                                                        color = if (fareGroup.second == "Regular Fare") TextVariant else IndianGreen,
                                                        fontWeight = if (fareGroup.second == "Regular Fare") FontWeight.Normal else FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 5. Help Support Banner
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD4E3FF)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("earnings_help_support_banner")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "info icon",
                                            tint = Color(0xFF001C3A),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Column {
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) "Having trouble with a payout?" else "भुगतान में कोई समस्या है?",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF001C3A)
                                            )
                                            Text(
                                                text = if (lang == LocaleStrings.Lang.EN) {
                                                    "Contact partner support for real-time resolution."
                                                } else {
                                                    "वास्तविक समय समाधान के लिए सहायता टीम से संपर्क करें।"
                                                },
                                                fontSize = 12.sp,
                                                color = Color(0xFF2F486A)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "profile" -> {
                    // PARTNER PORTAL PROFILE DETAILS
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // User Portrait Headshot avatar
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color.White, CircleShape)
                                    .border(3.dp, Saffron, CircleShape)
                                    .padding(4.dp)
                            ) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAoccup5UkdPY9IclbCnhpxSuSFJgWHYavjKn66G8pc8reQKz8TTijOeKi7OCX-mVvDNpWBzlNwDSg2ErZJ8T9BKgQviZ7cqRyTxMD6ox27WCz6QkzV98nPCyWYRzg6NgTIPqGwjB_XHfGFzdNIyPuLX2DP98NVk_6zK7yM9T9-k1czyO-Xy9W74CP4LJxN2I0cZ6mHYC5j4WQwjXb1yZlWJOL_ZVxs5rxzICGnOtaWPoYo1KpQfOXksJwjMBhDjAWUhewfDLiNYNuT",
                                    contentDescription = "Rider Avatar Details",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = profileName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = TextOnSurface
                                )

                                Text(
                                    text = "Rider ID: #DIDI-LOG-4091",
                                    color = TextVariant,
                                    fontSize = 12.sp
                                )

                                Row(
                                    modifier = Modifier
                                        .background(LightGreen, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = IndianGreen, modifier = Modifier.size(14.dp))
                                    Text(text = "4.93 Rating", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IndianGreen)
                                }
                            }

                            HorizontalDivider(color = BorderColor.copy(alpha = 0.3f))

                            // Profile items list
                            ProfileFieldRow(
                                title = if (lang == LocaleStrings.Lang.EN) "Primary Mobile Number" else "प्राथमिक मोबाइल नंबर",
                                value = "+91-98711-20942"
                            )
                            ProfileFieldRow(
                                title = if (lang == LocaleStrings.Lang.EN) "Zone Station Hub" else "जोन स्टेशन हब",
                                value = if (lang == LocaleStrings.Lang.EN) "Dwarka Sector 12 Hub, Delhi" else "द्वारका सेक्टर १२ हब, दिल्ली"
                            )
                            ProfileFieldRow(
                                title = if (lang == LocaleStrings.Lang.EN) "Emergency Helpline Contact" else "आपातकालीन हेल्पलाइन नंबर",
                                value = "+91-1800-DIDI-SOS"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Switch back to buyer application link
                            Button(
                                onClick = {
                                    viewModel.showPartnerDashboard = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("partner_switch_back_buyer_app")
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Switch", tint = Color.White)
                                    Text(
                                        text = if (lang == LocaleStrings.Lang.EN) "Switch to Desi Didi Buyer App" else "देशी दीदी ग्राहक ऐप पर स्विच करें",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = NavyLight, modifier = Modifier.size(22.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextOnSurface)
    }
}

@Composable
fun MilestoneRow(
    index: Int,
    text: String,
    currentStep: Int,
    isDone: Boolean,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (isDone) LightGreen else LightSaffron,
                        CircleShape
                    )
                    .border(
                        2.dp,
                        if (isDone) IndianGreen else Saffron.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = IndianGreen,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = index.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronDark
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (isDone) IndianGreen else BorderColor.copy(alpha = 0.5f))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = if (isDone) TextOnSurface else TextVariant.copy(alpha = 0.5f),
                fontWeight = if (currentStep == index) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun EarningItemRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextVariant, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = TextOnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileFieldRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, color = TextVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(
            text = value,
            color = TextOnSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F4F6), RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
    }
}

private data class EarningsGraphItem(
    val label: String,
    val value: Double,
    val percentage: Float,
    val isActive: Boolean,
    val isUpcoming: Boolean = false
)

