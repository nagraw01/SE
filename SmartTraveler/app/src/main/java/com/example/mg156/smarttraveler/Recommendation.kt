package com.example.mg156.smarttraveler

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Recommendation(
        @SerializedName ("meta") var meta: Meta?,
        @SerializedName ("response") var response: Response?
):Serializable {

    data class Response(
            @SerializedName ("suggestedFilters") var suggestedFilters: SuggestedFilters?,
            @SerializedName ("geocode") var geocode: Geocode?,
            @SerializedName ("headerLocation") var headerLocation: String?,
            @SerializedName ("headerFullLocation") var headerFullLocation: String?,
            @SerializedName ("headerLocationGranularity") var headerLocationGranularity: String?,
            @SerializedName ("query") var query: String?,
            @SerializedName ("totalResults") var totalResults: Int?,
            @SerializedName ("suggestedBounds") var suggestedBounds: SuggestedBounds?,
            @SerializedName ("groups") var groups: List<Group?>?
    ):Serializable {

        data class SuggestedBounds(
                @SerializedName ("ne") var ne: Ne?,
                @SerializedName ("sw") var sw: Sw?
        ):Serializable {

            data class Ne(
                    @SerializedName ("lat") var lat: Double?,
                    @SerializedName ("lng") var lng: Double?
            ):Serializable


            data class Sw(
                    @SerializedName ("lat") var lat: Double?,
                    @SerializedName ("lng") var lng: Double?
            ):Serializable
        }


        data class Group(
                @SerializedName ("type") var type: String?,
                @SerializedName ("name") var name: String?,
                @SerializedName ("items") var items: List<Item?>?
        ):Serializable {

            data class Item(
                    @SerializedName ("reasons") var reasons: Reasons?,
                    @SerializedName ("venue") var venue: Venue?,
                    @SerializedName ("referralId") var referralId: String?
            ):Serializable {

                data class Venue(
                        @SerializedName ("id") var id: String?,
                        @SerializedName ("name") var name: String?,
                        @SerializedName ("location") var location: Location?,
                        @SerializedName ("categories") var categories: List<Category?>?,
                        @SerializedName ("delivery") var delivery: Delivery?,
                        @SerializedName ("photos") var photos: Photos?,
                        @SerializedName ("venuePage") var venuePage: VenuePage?
                ):Serializable {

                    data class Delivery(
                            @SerializedName ("id") var id: String?,
                            @SerializedName ("url") var url: String?,
                            @SerializedName ("provider") var provider: Provider?
                    ):Serializable {

                        data class Provider(
                                @SerializedName ("name") var name: String?,
                                @SerializedName ("icon") var icon: Icon?
                        ):Serializable {

                            data class Icon(
                                    @SerializedName ("prefix") var prefix: String?,
                                    @SerializedName ("sizes") var sizes: List<Int?>?,
                                    @SerializedName ("name") var name: String?
                            ):Serializable
                        }
                    }


                    data class VenuePage(
                            @SerializedName ("id") var id: String?
                    ):Serializable


                    data class Category(
                            @SerializedName ("id") var id: String?,
                            @SerializedName ("name") var name: String?,
                            @SerializedName ("pluralName") var pluralName: String?,
                            @SerializedName ("shortName") var shortName: String?,
                            @SerializedName ("icon") var icon: Icon?,
                            @SerializedName ("primary") var primary: Boolean?
                    ):Serializable {

                        data class Icon(
                                @SerializedName ("prefix") var prefix: String?,
                                @SerializedName ("suffix") var suffix: String?
                        ):Serializable
                    }


                    data class Photos(
                            @SerializedName ("count") var count: Int?,
                            @SerializedName ("groups") var groups: List<Any?>?
                    ):Serializable


                    data class Location(
                            @SerializedName ("address") var address: String?,
                            @SerializedName ("crossStreet") var crossStreet: String?,
                            @SerializedName ("lat") var lat: Double?,
                            @SerializedName ("lng") var lng: Double?,
                            @SerializedName ("labeledLatLngs") var labeledLatLngs: List<LabeledLatLng?>?,
                            @SerializedName ("distance") var distance: Int?,
                            @SerializedName ("postalCode") var postalCode: String?,
                            @SerializedName ("cc") var cc: String?,
                            @SerializedName ("city") var city: String?,
                            @SerializedName ("state") var state: String?,
                            @SerializedName ("country") var country: String?,
                            @SerializedName ("formattedAddress") var formattedAddress: List<String?>?
                    ):Serializable {

                        data class LabeledLatLng(
                                @SerializedName ("label") var label: String?,
                                @SerializedName ("lat") var lat: Double?,
                                @SerializedName ("lng") var lng: Double?
                        ):Serializable
                    }
                }


                data class Reasons(
                        @SerializedName ("count") var count: Int?,
                        @SerializedName ("items") var items: List<Item?>?
                ):Serializable {

                    data class Item(
                            @SerializedName ("summary") var summary: String?,
                            @SerializedName ("type") var type: String?,
                            @SerializedName ("reasonName") var reasonName: String?
                    ):Serializable
                }
            }
        }


        data class Geocode(
                @SerializedName ("what") var what: String?,
                @SerializedName ("where") var where: String?,
                @SerializedName ("center") var center: Center?,
                @SerializedName ("displayString") var displayString: String?,
                @SerializedName ("cc") var cc: String?,
                @SerializedName ("geometry") var geometry: Geometry?,
                @SerializedName ("slug") var slug: String?,
                @SerializedName ("longId") var longId: String?
        ):Serializable {

            data class Center(
                    @SerializedName ("lat") var lat: Double?,
                    @SerializedName ("lng") var lng: Double?
            ):Serializable


            data class Geometry(
                    @SerializedName ("bounds") var bounds: Bounds?
            ):Serializable {

                data class Bounds(
                        @SerializedName ("ne") var ne: Ne?,
                        @SerializedName ("sw") var sw: Sw?
                ):Serializable {

                    data class Ne(
                            @SerializedName ("lat") var lat: Double?,
                            @SerializedName ("lng") var lng: Double?
                    ):Serializable


                    data class Sw(
                            @SerializedName ("lat") var lat: Double?,
                            @SerializedName ("lng") var lng: Double?
                    ):Serializable
                }
            }
        }


        data class SuggestedFilters(
                @SerializedName ("header") var header: String?,
                @SerializedName ("filters") var filters: List<Filter?>?
        ):Serializable {

            data class Filter(
                    @SerializedName ("name") var name: String?,
                    @SerializedName ("key") var key: String?
            ):Serializable
        }
    }


    data class Meta(
            @SerializedName ("code") var code: Int?,
            @SerializedName ("requestId") var requestId: String?
    ):Serializable
}