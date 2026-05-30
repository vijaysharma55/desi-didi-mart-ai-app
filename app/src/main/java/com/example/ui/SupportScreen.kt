package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = viewModel.currentLanguage
    var supportQuery by remember { mutableStateOf("") }

    // Multi-language strings for help & support
    val titleText = if (lang == LocaleStrings.Lang.EN) "Customer Support" else "कस्टमर सहायता"
    val heroTitle = if (lang == LocaleStrings.Lang.EN) "Namaste! How can we help?" else "नमस्ते! हम आपकी क्या सहायता कर सकते हैं?"
    val heroSubtitle = if (lang == LocaleStrings.Lang.EN) "We're here to make your shopping experience delightful." else "हम आपके खरीदारी अनुभव को सुखद बनाने के लिए यहाँ हैं।"
    val searchPlaceholder = if (lang == LocaleStrings.Lang.EN) "Search for 'Refund', 'Delay' or 'Dals'..." else "'रिफंड', 'वापसी', 'देरी' या 'दाल' खोजें..."
    val issuesTitle = if (lang == LocaleStrings.Lang.EN) "Common Issues" else "सामान्य समस्याएं"
    val recentTicketsTitle = if (lang == LocaleStrings.Lang.EN) "Recent Tickets" else "हाल ही के टिकट"
    val viewAllText = if (lang == LocaleStrings.Lang.EN) "View All" else "सभी देखें"
    val tutorialsTitle = if (lang == LocaleStrings.Lang.EN) "Quick Tutorials" else "त्वरित ट्यूटोरियल"
    val contactTitle = if (lang == LocaleStrings.Lang.EN) "Contact Us Directly" else "चैनल से सीधा संपर्क"
    val callSupportText = if (lang == LocaleStrings.Lang.EN) "Call Support (9 AM - 9 PM)" else "कॉल सपोर्ट (सुबह ९ - रात ९)"
    val chatWhatsAppText = if (lang == LocaleStrings.Lang.EN) "Chat on WhatsApp" else "व्हाट्सएप पर चैट करें"
    val emailText = if (lang == LocaleStrings.Lang.EN) "Email us at support@desididimart.com" else "हमे ईमेल करें: support@desididimart.com"
    val footerText = if (lang == LocaleStrings.Lang.EN) "Your satisfaction is our priority." else "आपकी संतुष्टि ही हमारी प्राथमिकता है।"
    val footerSubText = if (lang == LocaleStrings.Lang.EN) "Desi Didi Mart • Trusted Locally" else "देशी दीदी मार्ट • स्थानीय रूप से भरोसेमंद"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleText,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("support_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Profile",
                            tint = NavyBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    // Quick Language Toggle inside TopBar
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("support_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle Language",
                            tint = NavyBlue
                        )
                    }
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Hero Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(LightSaffron, Background)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Support Agent Logo",
                            tint = SaffronDark,
                            modifier = Modifier.size(54.dp)
                        )

                        Text(
                            text = heroTitle,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextOnSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = heroSubtitle,
                            fontSize = 13.sp,
                            color = TextVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = supportQuery,
                            onValueChange = { supportQuery = it },
                            placeholder = {
                                Text(
                                    text = searchPlaceholder,
                                    fontSize = 14.sp,
                                    color = TextVariant.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search icon",
                                    tint = TextVariant.copy(alpha = 0.6f)
                                )
                            },
                            trailingIcon = {
                                if (supportQuery.isNotEmpty()) {
                                    IconButton(onClick = { supportQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = TextVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Saffron,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextOnSurface,
                                unfocusedTextColor = TextOnSurface
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("support_search_input")
                        )
                    }
                }
            }

            // 2. Common Issues Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = issuesTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SupportCategoryItem(
                            title = if (lang == LocaleStrings.Lang.EN) "Orders" else "ऑर्डर्स",
                            icon = Icons.Default.ReceiptLong,
                            iconColor = SaffronDark,
                            bgColor = LightSaffron,
                            onClick = {
                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Showing your Order issue topics" else "आपके ऑर्डर से सम्बंधित विषय दिखा रहे हैं", Toast.LENGTH_SHORT).show()
                                supportQuery = if (lang == LocaleStrings.Lang.EN) "Order Delay" else "ऑर्डर में देरी"
                            },
                            modifier = Modifier.weight(1f).testTag("support_cat_orders")
                        )
                        SupportCategoryItem(
                            title = if (lang == LocaleStrings.Lang.EN) "Payments" else "भुगतान",
                            icon = Icons.Default.Payments,
                            iconColor = NavyLight,
                            bgColor = Color(0xFFECEEFF),
                            onClick = {
                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Checking payment diagnostics" else "भुगतान डायग्नोस्टिक्स की जांच", Toast.LENGTH_SHORT).show()
                                supportQuery = if (lang == LocaleStrings.Lang.EN) "Refund" else "रिफंड"
                            },
                            modifier = Modifier.weight(1f).testTag("support_cat_payments")
                        )
                        SupportCategoryItem(
                            title = if (lang == LocaleStrings.Lang.EN) "Delivery" else "डिलीवरी",
                            icon = Icons.Default.LocalShipping,
                            iconColor = IndianGreen,
                            bgColor = LightGreen,
                            onClick = {
                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Showing latest delivery support cases" else "नवीनतम डिलीवरी सहायता मामले", Toast.LENGTH_SHORT).show()
                                supportQuery = if (lang == LocaleStrings.Lang.EN) "Rider Location" else "राइडर स्थान"
                            },
                            modifier = Modifier.weight(1f).testTag("support_cat_delivery")
                        )
                        SupportCategoryItem(
                            title = if (lang == LocaleStrings.Lang.EN) "Account" else "खाता",
                            icon = Icons.Default.Person,
                            iconColor = TextVariant,
                            bgColor = Color(0xFFF1F3F4),
                            onClick = {
                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Opening wallet points guide" else "वॉलेट पॉइंट्स निर्देशिका खोल रहे हैं", Toast.LENGTH_SHORT).show()
                                supportQuery = if (lang == LocaleStrings.Lang.EN) "Wallet Points" else "वॉलेट पॉइंट्स"
                            },
                            modifier = Modifier.weight(1f).testTag("support_cat_account")
                        )
                    }
                }
            }

            // 3. Recent Tickets Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recentTicketsTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
                        )

                        Text(
                            text = viewAllText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronDark,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Displaying all historic support tickets!" else "सभी पुराने सहायता टिकट प्रदर्शित किए जा रहे हैं!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(4.dp)
                        )
                    }

                    // Ticket 1: Missing Item - Resolved
                    TicketCard(
                        title = if (lang == LocaleStrings.Lang.EN) "Missing Item: Tur Dal" else "खोई हुई वस्तु: अरहर दाल",
                        statusText = if (lang == LocaleStrings.Lang.EN) "Resolved" else "सुलझाया गया",
                        description = if (lang == LocaleStrings.Lang.EN) "Refund of ₹145 completed successfully." else "₹145 का रिफंड सफलतापूर्वक पूरा हो गया है।",
                        metaInfo = "Ticket ID: #45920 • 2 hrs ago",
                        isResolved = true,
                        onClick = {
                            Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Refund details sent on mail." else "रिफंड विवरण ईमेल पर भेजा गया है।", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ticket 2: Delayed Delivery - In Progress
                    TicketCard(
                        title = if (lang == LocaleStrings.Lang.EN) "Delayed Delivery" else "देरी से डिलीवरी",
                        statusText = if (lang == LocaleStrings.Lang.EN) "In Progress" else "प्रगति पर है",
                        description = if (lang == LocaleStrings.Lang.EN) "Agent assigned: Meera is checking with rider." else "एजेंट आवंटित: मीरा राइडर से संपर्क कर रही हैं।",
                        metaInfo = "Ticket ID: #46102 • 15 mins ago",
                        isResolved = false,
                        onClick = {
                            Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Connecting line with agent Meera...." else "एजेंट मीरा से संपर्क स्थापित किया जा रहा है...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // 4. Quick Tutorials - Bento Grid Style Layout
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tutorialsTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Large Featured Video Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable {
                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Playing Wallet Points Tutorial..." else "वॉलेट पॉइंट्स का त्वरित वीडियो शुरू हो रहा है...", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDovtUnOViKxTk95ATpxqdsXk8VygpEDDwMojizOGqewo6wzZnYla6D4M1q0xpgw10jckb0Oek7KfYvDl39sr7AuuS4rmgGpERLqbECwz1uis6_XXWCg5j8KFmzK07YqI8ZRaVD2cspk02K685dt1zFTGnDIbwoeEkV5EoOtIrtLZj61YpHWckJHmIrBhlcYAErOzvAkah8c9ueiFmMxlA9QyBY1HfpnDs-dsE8lKc88HLX4m0CXbhxMyM60QD_rDKE5wtWz9rfg8ym",
                                contentDescription = "Pantry kitchen tutorial banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Scrim color overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                        )
                                    )
                            )

                            // Tag & Information
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Saffron, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (lang == LocaleStrings.Lang.EN) "FEATURED" else "मुख्य वीडियो",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "How to use Wallet Points" else "कमाना और उपभोग: वॉलेट पॉइंट्स कैसे इस्तेमाल करें",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = "Play Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Text(
                                        text = "2:15 mins",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Bento Row of Mini Videos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Video Card 1
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .clickable {
                                    Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Playing Track Order Tutorial..." else "आदेश की स्थिति जांच विडियो शुरू हो रहा है...", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBmLKbpV4HHauUlFqFt2nMACbEn8JakaXbXWd8J9dyOtjhNYYUKs9fZ7-d_lUnkdeQawKa57HB16xViHhrZ-5ts7thKcScd4tu1F1fNdIZNbpk_At8zfnc4DQGN0J8EFBMFsHIu6iAOZDNxIcLQ16ZQbEY8Mal92mvqprnlncJ8_MGFnKVy3pQ-5b594hL_dzrmRv7yPtcdZyAWEwNJgxe1hLNTAkFzgJpWmc5WgSBy-9VlLTVWashOQYctjZ_wVJW1EEu-MsCnKLNl",
                                    contentDescription = "Basmati grains thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f))
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Track My Order" else "ऑर्डर ट्रैक करें",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                )
                            }
                        }

                        // Video Card 2
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .clickable {
                                    Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Playing Address Tutorial..." else "पता बदलने की विडियो गाइड शुरू हो रही है...", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBsXvktZ5o25YToTwf226yfjSvN8ZKnNcmYEvwn6EZ6IPBTp22QAJ2Lx9Y4WBn6958DDTlsZV8tQ-WpHVnPRxMOMLlFMsuMP255WNqK2o_n3abn5D-B6yjQi-nXgGD6UgB9oDPTK7yaqpfK67LSK947vXMLIRs1gXlbrDd_kngx0zBi3gSXFo0rUSaZVXzJ2LqJVIOIF88nvC56aWnsUqHjCsgjZ6Cyl_FnEhJRZVcj5eYMGRfxflqlOTDsttN6BuNu-2No15-_E9sJ",
                                    contentDescription = "Support agent vector thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f))
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Change Address" else "पता कैसे बदलें",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Contact Methods Directly
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = contactTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Call Support Button
                    Button(
                        onClick = {
                            Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Dialing Customer Care +91-1800-DESI..." else "डायल कर रहे हैं कस्टमर केयर +91-1800-DESI...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("support_call_btn")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call support icon",
                                tint = Color.White
                            )
                            Text(
                                text = callSupportText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Chat on WhatsApp
                    Button(
                        onClick = {
                            Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Opening WhatsApp helplinechat..." else "व्हाट्सएप हेल्पलाइन स्थापित कर रहा है...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Saffron),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("support_whatsapp_btn")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "WhatsApp help icon",
                                tint = IndianGreen
                            )
                            Text(
                                text = chatWhatsAppText,
                                color = SaffronDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Email Trigger Text
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, if (lang == LocaleStrings.Lang.EN) "Opening email client..." else "ईमेल प्रदाता खोल रहे हैं...", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = NavyLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = emailText,
                            fontSize = 13.sp,
                            color = NavyLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 6. Footer Content
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderColor.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = footerText,
                        fontSize = 13.sp,
                        color = TextVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = footerSubText.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextVariant.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SupportCategoryItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TicketCard(
    title: String,
    statusText: String,
    description: String,
    metaInfo: String,
    isResolved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Colored left stripe to match ticketing aesthetic
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .background(if (isResolved) IndianGreen else Saffron)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (isResolved) Icons.Default.CheckCircle else Icons.Default.Pending,
                    contentDescription = statusText,
                    tint = if (isResolved) IndianGreen else SaffronDark,
                    modifier = Modifier.size(20.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )

                        // Status badge label
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isResolved) LightGreen else LightSaffron,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusText.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResolved) IndianGreen else SaffronDark
                            )
                        }
                    }

                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = TextVariant
                    )

                    Text(
                        text = metaInfo,
                        fontSize = 10.sp,
                        color = TextVariant.copy(alpha = 0.5f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Details",
                    tint = TextVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}
