// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO2

package scala.protos.messages

@SerialVersionUID(0L)
final case class DataSet(
    data: _root_.scala.Seq[scala.protos.messages.Data] = _root_.scala.Seq.empty,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[DataSet] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      data.foreach { __item =>
        val __value = __item
        __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      }
      __size += unknownFields.serializedSize
      __size
    }
    override def serializedSize: _root_.scala.Int = {
      var read = __serializedSizeCachedValue
      if (read == 0) {
        read = __computeSerializedValue()
        __serializedSizeCachedValue = read
      }
      read
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): _root_.scala.Unit = {
      data.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      unknownFields.writeTo(_output__)
    }
    def clearData = copy(data = _root_.scala.Seq.empty)
    def addData(__vs: scala.protos.messages.Data*): DataSet = addAllData(__vs)
    def addAllData(__vs: Iterable[scala.protos.messages.Data]): DataSet = copy(data = data ++ __vs)
    def withData(__v: _root_.scala.Seq[scala.protos.messages.Data]): DataSet = copy(data = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => data
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PRepeated(data.iterator.map(_.toPMessage).toVector)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = scala.protos.messages.DataSet
    // @@protoc_insertion_point(GeneratedMessage[scala.protos.DataSet])
}

object DataSet extends scalapb.GeneratedMessageCompanion[scala.protos.messages.DataSet] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[scala.protos.messages.DataSet] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): scala.protos.messages.DataSet = {
    val __data: _root_.scala.collection.immutable.VectorBuilder[scala.protos.messages.Data] = new _root_.scala.collection.immutable.VectorBuilder[scala.protos.messages.Data]
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __data += _root_.scalapb.LiteParser.readMessage[scala.protos.messages.Data](_input__)
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    scala.protos.messages.DataSet(
        data = __data.result(),
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[scala.protos.messages.DataSet] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      scala.protos.messages.DataSet(
        data = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Seq[scala.protos.messages.Data]]).getOrElse(_root_.scala.Seq.empty)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = MessagesProto.javaDescriptor.getMessageTypes().get(1)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = MessagesProto.scalaDescriptor.messages(1)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 1 => __out = scala.protos.messages.Data
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = scala.protos.messages.DataSet(
    data = _root_.scala.Seq.empty
  )
  implicit class DataSetLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, scala.protos.messages.DataSet]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, scala.protos.messages.DataSet](_l) {
    def data: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Seq[scala.protos.messages.Data]] = field(_.data)((c_, f_) => c_.copy(data = f_))
  }
  final val DATA_FIELD_NUMBER = 1
  def of(
    data: _root_.scala.Seq[scala.protos.messages.Data]
  ): _root_.scala.protos.messages.DataSet = _root_.scala.protos.messages.DataSet(
    data
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[scala.protos.DataSet])
}
