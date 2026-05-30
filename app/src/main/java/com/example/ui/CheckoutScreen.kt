package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    product: Product,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lang = viewModel.currentLanguage

    // Interactive address state
    var showAddressDialog by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("Ravi Kumar") }
    var addressLine1 by remember { mutableStateOf("Flat No. 402, Sai Residency") }
    var addressLine2 by remember { mutableStateOf("Near Old Market, Indore") }
    var stateAndPincode by remember { mutableStateOf("Madhya Pradesh - 452001") }
    var phoneNumber by remember { mutableStateOf("+91 98765 43210") }

    // Temporary form state
    var editName by remember { mutableStateOf("") }
    var editLine1 by remember { mutableStateOf("") }
    var editLine2 by remember { mutableStateOf("") }
    var editStatePin by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }

    // Payment Option State (cod vs upi)
    var selectedPaymentMethod by remember { mutableStateOf("cod") }

    // Calculated fields
    val discountPercent = if (product.discountPercent > 0) product.discountPercent else 20
    val totalAmount = product.price.toInt()
    val itemsTotal = (product.price / (1 - discountPercent / 100.0)).toInt()
    val productDiscount = itemsTotal - totalAmount

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Checkout" else "चेकआउट",
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("checkout_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyBlue
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) "Need help? Contact support 24/7!" else "सहायता चाहिए? हमारे २४/७ सपोर्ट से संपर्क करें।",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = TextVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Fixed Bottom Checkout CTA Bar
            Surface(
                tonalElevation = 8.dp,
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E9EB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "GRAND TOTAL" else "कुल देय राशि",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextVariant,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "₹$totalAmount",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaffronDark
                        )
                    }

                    Button(
                        onClick = {
                            // Place Order
                            viewModel.purchaseProductDirectly(product)
                            viewModel.checkoutProduct = null
                            viewModel.selectedProductForDetails = null
                            
                            val successMessage = if (lang == LocaleStrings.Lang.EN) {
                                "Order Placed Successfully using ${selectedPaymentMethod.uppercase()}! Standard free shipping is active."
                            } else {
                                "${selectedPaymentMethod.uppercase()} के माध्यम से ऑर्डर सफलतापूर्वक स्वीकार किया गया!"
                            }
                            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("place_order_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "PLACE ORDER" else "ऑर्डर सबमिट करें",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Submit order icon",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7FAFC))
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Trust Banner (100% Secure Checkout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE2F3E7)) // 30% of transparent light green
                    .border(BorderStroke(1.dp, Color(0xFFC0E2CC)))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Secure lock icon",
                        tint = IndianGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "100% Secure Checkout" else "१००% सुरक्षित चेकआउट",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndianGreen
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Trusted by 50,000+ local customers" else "५०,०००+ स्थानीय ग्राहकों का अटूट भरोसा",
                            fontSize = 12.sp,
                            color = IndianGreen.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Shipping Address Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address Location",
                            tint = NavyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Shipping Address" else "शिपिंग पता",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    }

                    TextButton(
                        onClick = {
                            // Open Edit dialog
                            editName = customerName
                            editLine1 = addressLine1
                            editLine2 = addressLine2
                            editStatePin = stateAndPincode
                            editPhone = phoneNumber
                            showAddressDialog = true
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = NavyBlue),
                        modifier = Modifier.testTag("change_shipping_address_btn")
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Change" else "बदलें",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Customer Address Display Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E9EB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = customerName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                        Text(
                            text = "$addressLine1,\n$addressLine2,\n$stateAndPincode",
                            fontSize = 13.sp,
                            color = TextVariant,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = phoneNumber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextOnSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Order Summary Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Bag Icon",
                        tint = NavyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Order Summary" else "ऑर्डर विवरण",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E9EB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Product info row block with image
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(BorderStroke(1.dp, Color(0xFFEBEEF0)), RoundedCornerShape(8.dp))
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Qty: 1 Unit" else "मात्रा: १ इकाई",
                                    fontSize = 12.sp,
                                    color = TextVariant,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "₹$totalAmount",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronDark
                                    )
                                    Text(
                                        text = "₹$itemsTotal",
                                        fontSize = 12.sp,
                                        textDecoration = TextDecoration.LineThrough,
                                        color = TextVariant.copy(alpha = 0.5f)
                                    )
                                    Surface(
                                        color = LightGreen,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "$discountPercent% OFF",
                                            color = IndianGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F4F6), thickness = 1.dp)

                        // Detailed price calculation breakdown list
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Items Total" else "मूल्य योग",
                                    fontSize = 13.sp,
                                    color = TextVariant
                                )
                                Text(
                                    text = "₹$itemsTotal",
                                    fontSize = 13.sp,
                                    color = TextVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Product Discount" else "उत्पाद पर छूट",
                                    fontSize = 13.sp,
                                    color = IndianGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "-₹$productDiscount",
                                    fontSize = 13.sp,
                                    color = IndianGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Delivery Fee" else "वितरण शुल्क (डिलिवरी)",
                                    fontSize = 13.sp,
                                    color = TextVariant
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹40",
                                        fontSize = 13.sp,
                                        textDecoration = TextDecoration.LineThrough,
                                        color = TextVariant.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = if (lang == LocaleStrings.Lang.EN) "FREE" else "मुफ़्त",
                                        fontSize = 13.sp,
                                        color = IndianGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = Color(0xFFE5E9EB),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Total Amount" else "कुल देय राशि",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface
                                )
                                Text(
                                    text = "₹$totalAmount",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SaffronDark
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Payment Options Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Payments Icon",
                        tint = NavyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Payment Options" else "भुगतान के विकल्प",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                }

                // COD Primary Highlight Selection Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPaymentMethod == "cod") Color(0xFFFFF7F0) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (selectedPaymentMethod == "cod") 2.dp else 1.dp,
                        color = if (selectedPaymentMethod == "cod") Saffron else Color(0xFFE5E9EB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPaymentMethod = "cod" }
                        .testTag("payment_cod_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == "cod",
                            onClick = { selectedPaymentMethod = "cod" },
                            colors = RadioButtonDefaults.colors(selectedColor = Saffron)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Cash on Delivery (COD)" else "कैश ऑन डिलीवरी (COD)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface
                                )
                                Surface(
                                    color = LightSaffron,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = if (lang == LocaleStrings.Lang.EN) "MOST POPULAR" else "सबसे लोकप्रिय",
                                        color = SaffronDark,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Pay with cash when your order arrives" else "ऑर्डर पहुंचने पर नकद भुगतान करें",
                                fontSize = 12.sp,
                                color = TextVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Safe Icon",
                            tint = IndianGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // UPI Selection Card Block
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPaymentMethod == "upi") Color(0xFFFFF7F0) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (selectedPaymentMethod == "upi") 2.dp else 1.dp,
                        color = if (selectedPaymentMethod == "upi") Saffron else Color(0xFFE5E9EB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPaymentMethod = "upi" }
                        .testTag("payment_upi_card")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // UPI Header Inside
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F4F6))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "UPI (Google Pay, PhonePe, Paytm)" else "यूपीआई (गूगल पे, फोनपे, पेटीएम)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnSurface
                            )
                        }

                        // UPI Selector row list
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == "upi",
                                onClick = { selectedPaymentMethod = "upi" },
                                colors = RadioButtonDefaults.colors(selectedColor = Saffron)
                            )

                            // Wallet Icon Placeholder
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF1F4F6), RoundedCornerShape(8.dp))
                                    .border(BorderStroke(1.dp, Color(0xFFEBEEF0)), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "UPI Asset",
                                    tint = NavyBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Pay via any UPI App" else "किसी भी UPI ऐप द्वारा भुगतान करें",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextOnSurface,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Chevron Right",
                                tint = TextVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 5. Encrypted Transaction Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock Secure icon",
                    tint = TextVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "ENCRYPTED & SECURE TRANSACTION" else "एन्क्रिप्टेड और सुरक्षित लेनदेन",
                    fontSize = 11.sp,
                    color = TextVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }

    // Dynamic Edit Address Interactive modal dial
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Change Shipping Address" else "शिपिंग पता बदलें",
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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBlue),
                        modifier = Modifier.fillMaxWidth().testTag("edit_address_name")
                    )

                    OutlinedTextField(
                        value = editLine1,
                        onValueChange = { editLine1 = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Flat, House no., Apartment" else "कमरा नंबर, मकान, अपार्टमेंट", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBlue),
                        modifier = Modifier.fillMaxWidth().testTag("edit_address_line1")
                    )

                    OutlinedTextField(
                        value = editLine2,
                        onValueChange = { editLine2 = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Area, Colony, Street, Landmark" else "क्षेत्र, कॉलोनी, गली, स्थल चिन्ह", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBlue),
                        modifier = Modifier.fillMaxWidth().testTag("edit_address_line2")
                    )

                    OutlinedTextField(
                        value = editStatePin,
                        onValueChange = { editStatePin = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "State & Pincode" else "राज्य एवं पिनकोड", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBlue),
                        modifier = Modifier.fillMaxWidth().testTag("edit_address_state_pin")
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Mobile Number" else "मोबाइल नंबर", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBlue),
                        modifier = Modifier.fillMaxWidth().testTag("edit_address_phone")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editLine1.isNotBlank()) {
                            customerName = editName.trim()
                            addressLine1 = editLine1.trim()
                            addressLine2 = editLine2.trim()
                            stateAndPincode = editStatePin.trim()
                            phoneNumber = editPhone.trim()
                        }
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_address_btn")
                ) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Save Address" else "पता सहेजें",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
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
