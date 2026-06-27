package com.example.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── MarketingEvent ─────────────────────────────────────────────────

@Serializable
data class MarketingEventResponse(
    @SerialName("marketing_event") val marketingEvent: MarketingEventDto
)

@Serializable
data class MarketingEventsResponse(
    @SerialName("marketing_events") val marketingEvents: List<MarketingEventDto>
)

@Serializable
data class MarketingEventDto(
    val id: Long? = null,

    // "ad" | "post" | "message" | "retargeting" | "transactional" |
    // "affiliate" | "loyalty" | "newsletter" | "abandoned_cart"
    @SerialName("event_type") val eventType: String,

    // "search" | "display" | "social" | "email" | "referral"
    @SerialName("marketing_channel") val marketingChannel: String,

    @SerialName("remote_id") val remoteId: String? = null,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("ended_at") val endedAt: String? = null,
    @SerialName("scheduled_to_end_at") val scheduledToEndAt: String? = null,

    val budget: Double? = null,
    val currency: String? = null,          // e.g. "USD"

    // "daily" | "lifetime"
    @SerialName("budget_type") val budgetType: String? = null,

    // UTM tracking (required for attribution)
    @SerialName("utm_campaign") val utmCampaign: String? = null,
    @SerialName("utm_source") val utmSource: String? = null,
    @SerialName("utm_medium") val utmMedium: String? = null,
    @SerialName("utm_term") val utmTerm: String? = null,
    @SerialName("utm_content") val utmContent: String? = null,

    val description: String? = null,
    @SerialName("manage_url") val manageUrl: String? = null,
    @SerialName("preview_url") val previewUrl: String? = null,

    // Deprecated but still returned
    val paid: Boolean? = null,
    @SerialName("referring_domain") val referringDomain: String? = null,
    @SerialName("delivery_channel") val deliveryChannel: String? = null,
    @SerialName("breadcrumb_id") val breadcrumbId: String? = null,
    @SerialName("marketing_activity_id") val marketingActivityId: Long? = null,
    @SerialName("admin_graphql_api_id") val adminGraphqlApiId: String? = null,

    @SerialName("marketed_resources") val marketedResources: List<MarketedResourceDto> = emptyList()
)

@Serializable
data class MarketedResourceDto(
    val type: String,  // "product" | "collection" | "page" | "blog_post" | "discount"
    val id: Long
)

// ─── Engagements ────────────────────────────────────────────────────

@Serializable
data class EngagementsResponse(
    val engagements: List<EngagementDto>
)

@Serializable
data class EngagementDto(
    @SerialName("occurred_on") val occurredOn: String,      // "YYYY-MM-DD"
    @SerialName("fetched_at") val fetchedAt: String? = null,
    @SerialName("views_count") val viewsCount: Int? = null,
    @SerialName("impressions_count") val impressionsCount: Int? = null,
    @SerialName("clicks_count") val clicksCount: Int? = null,
    @SerialName("favorites_count") val favoritesCount: Int? = null,
    @SerialName("comments_count") val commentsCount: Int? = null,
    @SerialName("shares_count") val sharesCount: Int? = null,
    @SerialName("ad_spend") val adSpend: String? = null,   // decimal string, e.g. "10.0"
    @SerialName("currency_code") val currencyCode: String? = null,
    @SerialName("is_cumulative") val isCumulative: Boolean? = null,
    @SerialName("unsubscribes_count") val unsubscribesCount: Int? = null,
    @SerialName("complaints_count") val complaintsCount: Int? = null,
    @SerialName("fails_count") val failsCount: Int? = null,
    @SerialName("sends_count") val sendsCount: Int? = null,
    @SerialName("unique_views_count") val uniqueViewsCount: Int? = null,
    @SerialName("unique_clicks_count") val uniqueClicksCount: Int? = null,
    @SerialName("utc_offset") val utcOffset: String? = null
)

// ─── Request bodies ──────────────────────────────────────────────────

@Serializable
data class CreateMarketingEventRequest(
    @SerialName("marketing_event") val marketingEvent: MarketingEventRequestDto
)

@Serializable
data class MarketingEventRequestDto(
    @SerialName("event_type") val eventType: String,           // "ad"
    @SerialName("marketing_channel") val marketingChannel: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("utm_campaign") val utmCampaign: String,
    @SerialName("utm_source") val utmSource: String,
    @SerialName("utm_medium") val utmMedium: String,
    @SerialName("referring_domain") val referringDomain: String? = null,
    val paid: Boolean? = null,
    val budget: Double? = null,
    val currency: String? = null,
    @SerialName("budget_type") val budgetType: String? = null,
    @SerialName("ended_at") val endedAt: String? = null,
    @SerialName("scheduled_to_end_at") val scheduledToEndAt: String? = null,
    val description: String? = null,
    @SerialName("manage_url") val manageUrl: String? = null,
    @SerialName("preview_url") val previewUrl: String? = null,
    @SerialName("remote_id") val remoteId: String? = null,
    @SerialName("marketed_resources") val marketedResources: List<MarketedResourceDto>? = null
)

@Serializable
data class CreateEngagementsRequest(
    val engagements: List<EngagementDto>
)

// ─── Count (deprecated) ──────────────────────────────────────────────

@Serializable
data class CountResponse(
    val count: Int
)