package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = viewModel.currentLanguage
    val coinBalance = viewModel.didiCoinsBalance

    // Interactive Edit profile states
    var showEditDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Ravi Kumar") }
    var userPhone by remember { mutableStateOf("+91 98765 43210") }

    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Top App Bar matching میری پروफ़ाइल
        TopAppBar(
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "My Profile" else "मेरी प्रोफ़ाइल",
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    fontSize = 20.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.selectedTab = "home" },
                    modifier = Modifier.testTag("profile_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NavyBlue
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { 
                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) "Search for products from homepage!" else "मुख्य पृष्ठ से उत्पादों को खोजें!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NavyBlue
                    )
                }
            }
        )

        // 2. Profile Header matching high-fidelity orange panel block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightSaffron)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar picture with clean white circular border
                Box(
                    modifier = Modifier.size(96.dp)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC5RnPJB9HSzdv-XypvUzpd_9FA-uiBt4Gc0uRPsrzX4oARX2RzwVdevYNatE50L6qKfFdZJeVtUVa5WofNeSfXZcZsTZmCIPo5IBH_hFpGowyINM9f0i4yZvhylCaIgb9g8nlWhCxcOJ5CbU39ZZ7KC9oT6Q-LIQSKzC4aWnxdyGs45KO2BckLAAz0-s1BCvkoz-xb61dSseEAwqewuWvYs2C1Zk1og1DPl_duM2gP37A2UUHm-xeBNuh25KBqRUJwJRecYZqC2PUX",
                        contentDescription = userName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface,
                    modifier = Modifier.testTag("profile_user_name")
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userPhone,
                    fontSize = 14.sp,
                    color = TextVariant,
                    modifier = Modifier.testTag("profile_user_phone")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        editName = userName
                        editPhone = userPhone
                        showEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Saffron),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("edit_profile_btn")
                ) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Edit Profile" else "प्रोफ़ाइल बदलें",
                        color = SaffronDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 3. Quick Stats Grid Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: My Points (Coins)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0x0D000080)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.rewardDidiCoins(10)
                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) {
                                "Tap feedback: You earned 10 Coins!"
                            } else {
                                "टैप फीडबैक: आपको १० सिक्के मिले हैं!"
                            },
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .testTag("profile_coins_card"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Coins option icon",
                        tint = Saffron,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "MY POINTS" else "मेरे पॉइंट्स",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextVariant,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "$coinBalance Coins" else "$coinBalance सिक्के",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyLight
                    )
                }
            }

            // Card 2: Joined Duration
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0x0D000080)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) "Proud member since October 2023!" else "अक्टूबर २०२३ से सम्मानित सदस्य!",
                            Toast.LENGTH_SHORT
                        ).show()
                     },
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Joined timeline icon",
                        tint = Saffron,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "JOINED" else "जुड़े हुए हैं",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextVariant,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "October 2023" else "अक्टूबर 2023",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyLight
                    )
                }
            }
        }

        // 4. Menu Settings Cards aligning with mockup bento style
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0x0D000080)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column {
                // ROW 1: My Orders
                ProfileMenuRow(
                    title = if (lang == LocaleStrings.Lang.EN) "My Orders" else "मेरे ऑर्डर्स",
                    icon = Icons.Default.Receipt,
                    onClick = { viewModel.selectedTab = "orders" }
                )
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 2: My Addresses
                ProfileMenuRow(
                    title = if (lang == LocaleStrings.Lang.EN) "My Addresses" else "मेरे पते",
                    icon = Icons.Default.LocationOn,
                    onClick = {
                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) {
                                "Address: Flat No. 402, Sai Residency, Indore"
                            } else {
                                "पता: फ्लैट नंबर ४०२, साईं रेजीडेंसी, इंदौर"
                            },
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 3: Payment Methods
                ProfileMenuRow(
                    title = if (lang == LocaleStrings.Lang.EN) "Payment Methods" else "भुगतान के तरीके",
                    icon = Icons.Default.AccountBalanceWallet,
                    onClick = {
                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) {
                                "Pre-configured: UPI & Cash on Delivery (COD) are active!"
                            } else {
                                "पूर्व-कॉन्फ़िगर: UPI और कैश ऑन डिलीवरी (COD) चालू हैं!"
                            },
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 4: App Language Switcher Block
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language toggle",
                            tint = NavyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Language" else "भाषा",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF1F4F6), CircleShape)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        LanguageToggleBtn(
                            text = "English",
                            isSelected = lang == LocaleStrings.Lang.EN,
                            onClick = { viewModel.setLanguage(LocaleStrings.Lang.EN) }
                        )
                        LanguageToggleBtn(
                            text = "हिंदी",
                            isSelected = lang == LocaleStrings.Lang.HI,
                            onClick = { viewModel.setLanguage(LocaleStrings.Lang.HI) }
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 5: Notifications
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications list toggle",
                            tint = NavyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Notifications" else "सूचनाएं",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    }

                    var notificationsEnabled by remember { mutableStateOf(true) }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            Toast.makeText(
                                context,
                                if (it) {
                                    if (lang == LocaleStrings.Lang.EN) "Notifications enabled" else "सूचनाएं चालू"
                                } else {
                                    if (lang == LocaleStrings.Lang.EN) "Notifications muted" else "सूचनाएं बंद"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Saffron,
                            checkedTrackColor = LightSaffron
                        )
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 6: Refer & Earn Promo block
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(
                                context,
                                if (lang == LocaleStrings.Lang.EN) {
                                    "Referral Code 'RAVI100' Copied to Clipboard!"
                                } else {
                                    "रेफरल कोड 'RAVI100' क्लिपबोर्ड पर कॉपी किया गया!"
                                },
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redeem,
                            contentDescription = "Gift Icon",
                            tint = IndianGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Refer & Earn" else "रेफर करें और कमाएं",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnSurface
                            )
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "GET ₹100 REWARD" else "₹100 इनाम पाएं",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndianGreen
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go Icon",
                        tint = TextVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 7: Help and support care row
                ProfileMenuRow(
                    title = if (lang == LocaleStrings.Lang.EN) "Help & Support" else "सहायता और सपोर्ट",
                    icon = Icons.Default.Help,
                    onClick = {
                        viewModel.showSupportScreen = true
                    }
                )
                HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                // ROW 8: Delivery Partner Portal login option
                ProfileMenuRow(
                    title = if (lang == LocaleStrings.Lang.EN) "Didi Delivery Partner" else "दीदी डिलीवरी पार्टनर",
                    icon = Icons.Default.DeliveryDining,
                    onClick = {
                        viewModel.showPartnerLoginScreen = true
                    }
                )
            }
        }

        // 5. Secure Outlined Logout Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        if (lang == LocaleStrings.Lang.EN) "Logged out successfully (Simulation)" else "सफलतापूर्वक लॉगआउट किया गया (सिमुलेशन)",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ErrorColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_btn")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout icon",
                        tint = ErrorColor
                    )
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Logout" else "लॉगआउट",
                        color = ErrorColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Version number conforming strictly to mockup "App Version 2.4.1 (Stable)"
            Text(
                text = if (lang == LocaleStrings.Lang.EN) "App Version 2.4.1 (Stable)" else "ऐप संस्करण 2.4.1 (स्टेबल)",
                fontSize = 12.sp,
                color = TextVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // 6. Interactive Modal to Edit Profile details dynamically
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Edit Profile Information" else "अपनी प्रोफ़ाइल बदलें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyBlue
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Full Name" else "पूरा नाम", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Saffron),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_profile_name_input")
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Mobile Number" else "मोबाइल नंबर", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Saffron),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_profile_phone_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editPhone.isNotBlank()) {
                            userName = editName.trim()
                            userPhone = editPhone.trim()
                            Toast.makeText(
                                context,
                                if (lang == LocaleStrings.Lang.EN) {
                                    "Profile Details updated successfully!"
                                } else {
                                    "प्रोफ़ाइल विवरण सफलतापूर्वक अपडेट किया गया!"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_profile_details_btn")
                ) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Save Details" else "सहेजें",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Cancel" else "रद्द करें",
                        color = TextVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = NavyLight,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnSurface
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go",
            tint = TextVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun LanguageToggleBtn(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Saffron else Color.Transparent,
        shape = CircleShape,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}
